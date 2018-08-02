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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class ScoutController
{

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
        InformationHistory informationHistory = new InformationHistory(seq, new Date(), reportLostRequest.getMessage());

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

        System.out.println("in update location");
        String lat = updateUserLocationRequest.getLat();
        String lon = updateUserLocationRequest.getLon();
        List<String> beaconsIds = updateUserLocationRequest.getBeaconsIds();

        if (Utils.isDouble(lat) && Utils.isDouble(lon))
        {
            GeoPoint geoPoint = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));

            //save user date
            List<Visitor> visitors = this.visitorRepository.findByBeaconIdIn(beaconsIds);
            List<Group> groups = this.groupRepository.findAll();

            Date currentTime = new Date();

            List<VisitorSituation> visitorSituations = visitorSituationRepository.findByVisitorIn(visitors);

            // for each visitor check is he in the geo fence lastLocation of the current time.
            for (Visitor visitor : visitors)
            {
                //get geo fence of the visitor

                Group group = groups.stream().filter(g -> g.getId().equals(visitor.getGroupId())).findFirst().orElse(null);

                if (group != null)
                {
                    TimedGeoFence geoFence = group.getTimedGeoFences().stream().filter(timedGeoFence -> timedGeoFence.getFrom().before(currentTime) && timedGeoFence.getTo().after(currentTime)).findFirst().orElse(null);

                    if (geoFence != null && geoFence.getGeoPoints() != null)
                    {
                        VisitorSituation visitorSituation = visitorSituations.stream().filter(f -> f.getVisitor().equals(visitor)).findFirst().orElse(null);

                        if (visitorSituation == null)
                        {
                            visitorSituation = new VisitorSituation(ObjectId.get(), visitor, geoPoint, SituationStatus.Normal, null, null, null);
                            visitorSituations.add(visitorSituation);
                        }
                        else
                        {
                            visitorSituation.setLastLocation(geoPoint);
                        }


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
//                        throw new CustomException(ErrorCode.CLAN_MISS_CURRENT_GEO_FENCE);
                        System.out.println(ErrorCode.CLAN_MISS_CURRENT_GEO_FENCE);
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

}
