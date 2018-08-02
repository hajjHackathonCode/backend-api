package com.campaigns.api.controller;

import com.campaigns.api.model.Group;
import com.campaigns.api.model.TimedGeoFence;
import com.campaigns.api.model.Visitor;
import com.campaigns.api.repository.GroupRepository;
import com.campaigns.api.repository.VisitorRepository;
import com.campaigns.api.utils.CustomException;
import com.campaigns.api.utils.ErrorCode;
import com.campaigns.api.utils.JsonBody;
import com.campaigns.api.utils.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AdminController
{
    private final GroupRepository groupRepository;

    private final VisitorRepository visitorRepository;

    @Autowired
    public AdminController(GroupRepository groupRepository, VisitorRepository visitorRepository)
    {
        this.groupRepository = groupRepository;
        this.visitorRepository = visitorRepository;
    }

    @GetMapping("/groups")
    public ResponseEntity<List<Group>> getClans()
    {
        return ResponseEntity.ok(groupRepository.findAll());
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable ObjectId id)
    {
        return ResponseEntity.ok(groupRepository.findOne(id));
    }

    @PostMapping(value = "/groups/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addGroup(@RequestBody Group group) throws CustomException
    {
        return Utils.checkMongoSave(groupRepository.save(group), true);
    }

    @PostMapping("/users/addusers")
    public ResponseEntity<?> addBeacons(@RequestBody List<Visitor> visitors) throws CustomException
    {
        HashMap<String, String> duplicateTester = new LinkedHashMap<>();

        List<String> beaconIds = visitors.stream().map(Visitor::getBeaconId).collect(Collectors.toList());

        String duplicatedBeaconId = null;
        for (String beaconId : beaconIds)
        {
            try
            {
                duplicatedBeaconId = beaconId;
                duplicateTester.put(beaconId, beaconId);
            }
            catch (Exception ignored)
            {
                throw new CustomException(ErrorCode.DUPLICATE_BEACON_ID, duplicatedBeaconId);
            }

        }

        return Utils.checkMongoSave(this.visitorRepository.save(visitors), true);
    }

    @PostMapping("/groups/removegeofence")
    public ResponseEntity<?> removeGeoFence(@RequestBody RemoveGeoFenceRequest req) throws CustomException
    {
        Group group = groupRepository.findOne(req.getId());

        ObjectId fenceId = req.getFenceId();

        if (group != null && group.getTimedGeoFences() != null)
        {
            List<TimedGeoFence> timedGeoFences = new ArrayList<>();


            for (TimedGeoFence timedGeoFence : group.getTimedGeoFences())
            {
                if (!timedGeoFence.getId().equals(fenceId))
                {
                    timedGeoFences.add(timedGeoFence);
                }
            }

            group.setTimedGeoFences(timedGeoFences);
            groupRepository.save(group);
        }
        return ResponseEntity.ok(new JsonBody<>(fenceId.toHexString()));
    }

    @PostMapping("/groups/addgeofence")
    public ResponseEntity<?> addGeoFence(@RequestBody TimedGeoFence timedGeoFence) throws CustomException
    {
        Group group = groupRepository.findOne(timedGeoFence.getId());

        if (group != null)
        {
            ObjectId fenceId = ObjectId.get();

            List<TimedGeoFence> timedGeoFences = group.getTimedGeoFences();

            if (timedGeoFences == null)
                timedGeoFences = new ArrayList<>();

            timedGeoFence.setId(fenceId);
            timedGeoFences.add(timedGeoFence);

            group.setTimedGeoFences(timedGeoFences);

            Group savedGroup = groupRepository.save(group);

            return Utils.checkMongoSave(savedGroup, fenceId.toHexString());
        }
        else
        {
            throw new CustomException(ErrorCode.OBJECT_ID_NOT_FOUND);
        }
    }
}
