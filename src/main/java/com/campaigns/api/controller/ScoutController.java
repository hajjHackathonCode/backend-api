package com.campaigns.api.controller;

import com.campaigns.api.model.*;
import com.campaigns.api.repository.GroupRepository;
import com.campaigns.api.repository.VisitorRepository;
import com.campaigns.api.repository.VisitorSituationRepository;
import com.campaigns.api.services.GeoPolygonServices;
import com.campaigns.api.utils.CustomException;
import com.campaigns.api.utils.ErrorCode;
import com.campaigns.api.utils.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/users/getalllosts")
    public ResponseEntity<?> getAllLostVisitors()
    {
        List<VisitorSituation> visitorSituations = visitorSituationRepository.findAllBySituationStatus(SituationStatus.Lost);
        if (visitorSituations != null && visitorSituations.size() > 0)
        {
            List<String> beaconsIds = visitorSituations.stream().map(m -> m.getVisitor().getBeaconId()).collect(Collectors.toList());
            return ResponseEntity.ok(beaconsIds);
        }
        else
        {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/groups/getstatus")
    public ResponseEntity<?> getGroupStatus(ObjectId groupId)
    {
        // get visitor situations of the group
        List<VisitorSituation> visitorSituations = visitorSituationRepository.findByGroupId(groupId);

        // if there is lost visitor return its beacon id
        List<String> beaconIds = visitorSituations.stream().filter(f -> f.getSituationStatus().equals(SituationStatus.Lost)).map(m -> m.getVisitor().getBeaconId()).collect(Collectors.toList());

        if (beaconIds != null && beaconIds.size() > 0)
            return ResponseEntity.ok(beaconIds);
        else
            return ResponseEntity.ok(true);
    }

    @PostMapping("/users/updateLocations")
    public ResponseEntity<?> updateUserLocations(@RequestBody UpdateUserLocationRequest updateUserLocationRequest) throws CustomException
    {

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

            // for each visitor check is he in the geo fence location of the current time.
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
                            visitorSituation = new VisitorSituation(ObjectId.get(), visitor, geoPoint, SituationStatus.Normal, null, null);
                            visitorSituations.add(visitorSituation);
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
