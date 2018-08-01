package com.clanconnect.api.controller;

import com.clanconnect.model.GeoLocation;
import com.clanconnect.model.Visitor;
import com.clanconnect.repository.UserSituationRepository;
import com.clanconnect.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScoutController
{
    private final UserSituationRepository userSituationRepository;
    private final VisitorRepository visitorRepository;

    @Autowired
    public ScoutController(UserSituationRepository userSituationRepository, VisitorRepository visitorRepository)
    {
        this.userSituationRepository = userSituationRepository;
        this.visitorRepository = visitorRepository;
    }

    @PostMapping("/users/update-locations")
    public ResponseEntity<Boolean> updateUserLocations(String lat, String lon, List<String> beaconsIds)
    {
        //save user date
        List<Visitor> visitors = this.visitorRepository.findByBeaconIdIn(beaconsIds);



        visitors.stream().forEach(visitor -> {
            GeoLocation geoLocation = new GeoLocation(lat, lon);

        });

    }
}
