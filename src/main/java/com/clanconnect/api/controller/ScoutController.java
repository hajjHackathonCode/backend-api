package com.clanconnect.api.controller;

import com.clanconnect.api.model.*;
import com.clanconnect.api.repository.VisitorRepository;
import com.clanconnect.api.repository.VisitorSituationRepository;
import com.clanconnect.api.services.GeoPolygonServices;
import com.clanconnect.api.utils.CustomException;
import com.clanconnect.api.utils.ErrorCode;
import com.clanconnect.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class ScoutController
{
    private final VisitorSituationRepository visitorSituationRepository;
    private final GeoPolygonServices geoPolygonServices;
    private final VisitorRepository visitorRepository;

    @Autowired
    public ScoutController(VisitorSituationRepository visitorSituationRepository, VisitorRepository visitorRepository, GeoPolygonServices geoPolygonServices)
    {
        this.visitorSituationRepository = visitorSituationRepository;
        this.visitorRepository = visitorRepository;
        this.geoPolygonServices = geoPolygonServices;
    }

    @PostMapping("/users/update-locations")
    public ResponseEntity<Boolean> updateUserLocations(String lat, String lon, List<String> beaconsIds) throws CustomException
    {
        if (Utils.isDouble(lat) && Utils.isDouble(lon))
        {
            GeoPoint geoPoint = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));

            //save user date
            List<Visitor> visitors = this.visitorRepository.findByBeaconIdIn(beaconsIds);

            Date currentTime = new Date();

            List<VisitorSituation> visitorSituations = visitorSituationRepository.findByVisitorIn(visitors);

            // for each visitor check is he in the geo fence location of the current time.
            for (Visitor visitor : visitors)
            {
                //get geo fence of the visitor
                TimedGeoFence geoFence = visitor.getClan().getTimedGeoFence().stream().filter(timedGeoFence -> timedGeoFence.getFrom().before(currentTime) && timedGeoFence.getTo().after(currentTime)).findFirst().orElse(null);

                if (geoFence != null && geoFence.getGeoPoints() != null)
                {
                    VisitorSituation visitorSituation = visitorSituations.stream().filter(f -> f.getVisitor().equals(visitor)).findFirst().orElse(null);

                    if (visitorSituation == null)
                        visitorSituation = new VisitorSituation(visitor, geoPoint, SituationStatus.Normal, null, null);


                    //check if the visitor is NOT inside the geo fence of the clan in the current time
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

                    visitorSituations.add(visitorSituation);
                }
                else
                {
                    throw new CustomException(ErrorCode.CLAN_MISS_CURRENT_GEO_FENCE);
                }
            }
        }
        else
            throw new CustomException(ErrorCode.INPUT_INVALID);
    }
}
