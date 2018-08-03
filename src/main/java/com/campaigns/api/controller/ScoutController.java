package com.campaigns.api.controller;

import com.campaigns.api.model.*;
import com.campaigns.api.repository.GroupRepository;
import com.campaigns.api.repository.VisitorRepository;
import com.campaigns.api.repository.VisitorSituationRepository;
import com.campaigns.api.services.GeoPolygonServices;
import com.campaigns.api.utils.CustomException;
import com.campaigns.api.utils.ErrorCode;
import com.campaigns.api.utils.JsonBody;
import com.campaigns.api.utils.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ScoutController
{

    @Value("${APP_MODE:}")
    private String appMode;

    @Value("${sender.image:}")
    private String senderImage;

    @Value("${sender.name:حسن ايوب}")
    private String senderName;

    private final GroupRepository groupRepository;
    private final VisitorSituationRepository visitorSituationRepository;
    private final GeoPolygonServices geoPolygonServices;

    private final VisitorRepository visitorRepository;

    @Autowired
    public ScoutController(VisitorSituationRepository visitorSituationRepository, VisitorRepository visitorRepository, GeoPolygonServices geoPolygonServices, GroupRepository groupRepository)
    {
        this.visitorSituationRepository = visitorSituationRepository;
        this.visitorRepository = visitorRepository;
        this.geoPolygonServices = geoPolygonServices;
        this.groupRepository = groupRepository;
    }

    /**
     * get list of all lost visitors among all groups
     *
     * @return list of beacons ids of all lost visitors
     */

    @GetMapping("/users/getalllosts")
    public ResponseEntity<?> getAllLostVisitors()
    {
        List<VisitorSituation> visitorSituations = visitorSituationRepository.findAllBySituationStatus(SituationStatus.Lost);
        if (visitorSituations != null && visitorSituations.size() > 0)
        {
            VisitorSituation.fillLostPeriodIfAny(visitorSituations, visitorRepository);
            return ResponseEntity.ok(visitorSituations);
        }
        else
        {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/users/getfriendstatus/{beaconId}")
    public ResponseEntity<?> getFriendStatus(@PathVariable String beaconId) throws CustomException
    {
        Visitor visitor = visitorRepository.findByBeaconId(beaconId);

        if (visitor != null)
        {
            VisitorSituation situation = visitorSituationRepository.findByVisitorBeaconId(visitor.getBeaconId());
            VisitorSituation.fillLostPeriodIfAny(situation, visitorRepository);
            return JsonBody.ok(situation.getSituationStatus());
        }
        else
            throw new CustomException(ErrorCode.OBJECT_NOT_FOUND, "visitor not found");
    }

    @GetMapping("/users/visitors")
    public ResponseEntity<?> getAllVisitors() throws CustomException
    {
        return ResponseEntity.ok(visitorRepository.findAll());
    }

    @GetMapping("/users/visitor/{beaconId}")
    public ResponseEntity<?> getAllVisitors(@PathVariable String beaconId) throws CustomException
    {
        return ResponseEntity.ok(visitorRepository.findByBeaconId(beaconId));
    }

    @PostMapping("/users/visitor/{beaconId}/name")
    public ResponseEntity<?> postVisitorName(@PathVariable String beaconId, @RequestParam String firstName, @RequestParam String lastName) throws CustomException
    {
        Visitor visitor = visitorRepository.findByBeaconId(beaconId);

        visitor.setFirstName(firstName);
        visitor.setLastName(lastName);

        return Utils.checkMongoSave(visitorRepository.save(visitor), true);
    }

    @PostMapping("/users/visitor/{beaconId}/image")
    public ResponseEntity<?> postVisitorImage(@PathVariable String beaconId, @RequestParam String image) throws CustomException
    {
        Visitor visitor = visitorRepository.findByBeaconId(beaconId);

        visitor.setImage(image);

        return Utils.checkMongoSave(visitorRepository.save(visitor), true);
    }

    @GetMapping("/users/situation/{beaconId}")
    public ResponseEntity<?> getSituation(@PathVariable String beaconId) throws CustomException
    {
        Visitor visitor = visitorRepository.findByBeaconId(beaconId);
        return ResponseEntity.ok(visitorSituationRepository.findByVisitorBeaconId(visitor.getBeaconId()));
    }

    @PostMapping("/users/reportlostinfo")
    public ResponseEntity<?> postReportLostInfo(@RequestBody ReportLostInfoRequest reportLostRequest) throws CustomException
    {
        Visitor visitor = visitorRepository.findByBeaconId(reportLostRequest.getBeaconId());

        VisitorSituation situation = visitorSituationRepository.findByVisitorBeaconId(visitor.getBeaconId());

        if (situation == null)
        {
            situation = new VisitorSituation(ObjectId.get(), visitor, reportLostRequest.getPoint(), SituationStatus.Lost, null, new Date(), null);
        }
        else
        {
            situation.setSituationStatus(SituationStatus.Lost);
        }

        if (situation.getInformationHistory() == null) situation.setInformationHistory(new ArrayList<>());

        int seq = situation.getInformationHistory().size();
        seq++;

        InformationHistory informationHistory = new InformationHistory(seq, new Date(), reportLostRequest.getMessage(), senderImage, senderName);

        situation.getInformationHistory().add(informationHistory);

        return Utils.checkMongoSave(visitorSituationRepository.save(situation), true);
    }


    @PostMapping("/users/reportlost")
    public ResponseEntity<?> postReportLost(@RequestBody ReportLostRequest reportLostRequest) throws CustomException
    {
        Visitor visitor = visitorRepository.findByBeaconId(reportLostRequest.getBeaconId());

        VisitorSituation situation = visitorSituationRepository.findByVisitorBeaconId(visitor.getBeaconId());

        if (situation == null)
        {
            situation = new VisitorSituation(ObjectId.get(), visitor, reportLostRequest.getPoint(), SituationStatus.Lost, null, new Date(), null);
        }
        else
        {
            situation.setLostStart(new Date());
            situation.setSituationStatus(SituationStatus.Lost);
        }

        return Utils.checkMongoSave(visitorSituationRepository.save(situation), true);
    }


    @GetMapping("/users/situations")
    public ResponseEntity<?> getAllSituation() throws CustomException
    {
        return ResponseEntity.ok(visitorSituationRepository.findAll());
    }

    /**
     * This method will return the group status and if there is any lost visitor in the group
     *
     * @param groupId the id of the group to get their status
     * @return return either true if there is no lost visitor, or a list of beacons of lost visitors.
     */
    @GetMapping("/groups/getstatus/{groupId}")
    public ResponseEntity<?> getGroupStatus(@PathVariable ObjectId groupId)
    {
        // get visitor situations of the group
        List<VisitorSituation> visitorSituations = visitorSituationRepository.findByGroupIdAndSituationStatus(groupId, SituationStatus.Lost);

        if (visitorSituations != null && visitorSituations.size() > 0)
        {
            VisitorSituation.fillLostPeriodIfAny(visitorSituations, visitorRepository);
            return JsonBody.ok(visitorSituations);
        }
        else
            return JsonBody.ok(true);
    }

    @PostMapping("/users/updateLocations")
    public ResponseEntity<?> updateUserLocations(@RequestBody UpdateUserLocationRequest updateUserLocationRequest) throws CustomException
    {
        Utils.debug("in update location", null);
        Utils.logObject(updateUserLocationRequest);

        String lat = updateUserLocationRequest.getLat();
        String lon = updateUserLocationRequest.getLon();
        List<String> beaconsIds = updateUserLocationRequest.getBeaconsIds();

        if (Utils.isDouble(lat) && Utils.isDouble(lon))
        {
            GeoPoint geoPoint = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));

            //save user date
            List<Group> groups = this.groupRepository.findAll();

            Date currentTime = new Date();

            List<VisitorSituation> visitorSituations = this.getAndRemoveSituation(beaconsIds, geoPoint);

            // for each visitor check is he in the geo fence lastLocation of the current time.
            for (VisitorSituation visitorSituation : visitorSituations)
            {
                // NOTE: This if block is for the demo only. In production it will be ignored
                if ("demo".equalsIgnoreCase(appMode))
                {
                    Utils.debug("running in demo mode", new Exception());

                    if ("out".equalsIgnoreCase(updateUserLocationRequest.getDir()))
                    {
                        visitorSituation.setSituationStatus(SituationStatus.Lost);
                        visitorSituation.setLostStart(new Date());
                        visitorSituation.setLastLocation(geoPoint);
                        Utils.debug("situation of the visitor became lost", new Exception());
                        continue;
                    }
                }

                //get geo fence of the visitor
                Group group = groups.stream().filter(g -> g.getId().equals(visitorSituation.getVisitor().getGroupId())).findFirst().orElse(null);

                if (group != null)
                {
                    TimedGeoFence geoFence = group.getTimedGeoFences().stream().filter(timedGeoFence -> timedGeoFence.getFrom().before(currentTime) && timedGeoFence.getTo().after(currentTime)).findFirst().orElse(null);

                    if (geoFence != null && geoFence.getGeoPoints() != null)
                    {
                        //check if the visitor is NOT inside the geo fence of the group in the current time
                        if (!geoPolygonServices.coordinateIsInsidePolygon(geoPoint, geoFence.getGeoPoints()))
                        {
                            // the user is in the warning status

                            // register the visitor as warning
                            visitorSituation.setSituationStatus(SituationStatus.Warning);

                            // check if the lost time passed two hour

                            Date lostTime = visitorSituation.getLostStart();

                            if (lostTime != null)
                            {
                                if (Utils.didPassedTime(Calendar.HOUR_OF_DAY, 2, lostTime, currentTime))
                                {
                                    visitorSituation.setSituationStatus(SituationStatus.Lost);
                                }
                            }
                            else
                            {
                                // record the visitor as lost.
                                visitorSituation.setLostStart(currentTime);
                            }
                        }
                        else
                        {
                            visitorSituation.setSituationStatus(SituationStatus.Normal);
                            visitorSituation.setLostStart(null);
                        }
                    }
                    else
                    {
//                        throw new CustomException(ErrorCode.GROUP_MISS_CURRENT_GEO_FENCE);
                        System.out.println(ErrorCode.GROUP_MISS_CURRENT_GEO_FENCE);
                    }
                }
                else
                    throw new CustomException(ErrorCode.OBJECT_ID_NOT_FOUND);

            }

            List<VisitorSituation> saved = visitorSituationRepository.save(visitorSituations);
            return Utils.checkMongoSave(saved, true);
        }
        else
            throw new CustomException(ErrorCode.INPUT_INVALID);
    }

    private List<VisitorSituation> getAndRemoveSituation(List<String> beaconIds, GeoPoint geoPoint) throws CustomException
    {

        if (beaconIds == null || beaconIds.size() == 0)
            throw new CustomException(ErrorCode.INPUT_INVALID);


        List<Visitor> visitors = visitorRepository.findByBeaconIdIn(beaconIds);
        List<VisitorSituation> situations = visitorSituationRepository.findByVisitorBeaconIdIn(beaconIds);
        visitorSituationRepository.delete(situations);


        List<VisitorSituation> cleanSituations = new ArrayList<>();

        // any beacon id that does not have situation, add situation for it as normal
        for (String beaconId : beaconIds)
        {
            List<VisitorSituation> foundSituations = situations.stream().filter(s -> beaconId.equalsIgnoreCase(s.getVisitor().getBeaconId())).collect(Collectors.toList());

            boolean situationNotFound = foundSituations.size() == 0;
            boolean situationDuplicate = foundSituations.size() > 1;

            if (situationNotFound)
            {
                Visitor visitor = visitors.stream().filter(f -> f.getBeaconId().equalsIgnoreCase(beaconId)).findFirst().orElse(null);
                VisitorSituation visitorSituation = new VisitorSituation(ObjectId.get(), visitor, geoPoint, SituationStatus.Normal, null, null, null);
                cleanSituations.add(visitorSituation);
            }
            else if (situationDuplicate)
            {
                SituationStatus situationStatus = null;
                Date lostStart = null;
                GeoPoint lostPoint = null;

                for (VisitorSituation foundSituation : foundSituations)
                {
                    if (foundSituation.getSituationStatus().equals(SituationStatus.Lost))
                    {
                        situationStatus = SituationStatus.Lost;
                        lostStart = foundSituation.getLostStart();
                        lostPoint = foundSituation.getLastLocation();
                    }
                    else if (foundSituation.getSituationStatus().equals(SituationStatus.Warning) && !SituationStatus.Lost.equals(situationStatus))
                    {
                        situationStatus = SituationStatus.Lost;
                        lostStart = foundSituation.getLostStart();
                        lostPoint = foundSituation.getLastLocation();
                    }
                    else if (foundSituation.getSituationStatus().equals(SituationStatus.Normal) && !SituationStatus.Warning.equals(situationStatus))
                    {
                        situationStatus = SituationStatus.Normal;
                        lostStart = null;
                        lostPoint = null;
                    }
                }

                Visitor visitor = visitors.stream().filter(f -> f.getBeaconId().equalsIgnoreCase(beaconId)).findFirst().orElse(null);
                VisitorSituation visitorSituation = new VisitorSituation(ObjectId.get(), visitor, lostPoint, situationStatus, null, lostStart, null);
                cleanSituations.add(visitorSituation);
            }
            else
                cleanSituations.add(foundSituations.get(0));
        }

        return cleanSituations;
    }

}
