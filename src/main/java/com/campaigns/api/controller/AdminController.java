package com.campaigns.api.controller;

import com.campaigns.api.model.Group;
import com.campaigns.api.model.TimedGeoFence;
import com.campaigns.api.model.Visitor;
import com.campaigns.api.repository.GroupRepository;
import com.campaigns.api.repository.VisitorRepository;
import com.campaigns.api.utils.CustomException;
import com.campaigns.api.utils.ErrorCode;
import com.campaigns.api.utils.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/groups/${id}")
    public ResponseEntity<Group> getGroup(@PathVariable ObjectId id)
    {
        return ResponseEntity.ok(groupRepository.findOne(id));
    }

    @PostMapping(value = "/groups/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addGroup(@RequestBody Group group) throws CustomException
    {
        return Utils.checkMongoSave(groupRepository.save(group), true);
    }

    @PostMapping("/users/addusers")
    public ResponseEntity<Boolean> addBeacons(@RequestBody List<Visitor> visitors) throws CustomException
    {
        return Utils.checkMongoSave(this.visitorRepository.save(visitors), true);
    }

    @PostMapping("/groups/removegeofence")

    public ResponseEntity<String> removeGeoFence(ObjectId id, ObjectId fenceId) throws CustomException
    {
        Group group = groupRepository.findOne(id);

        if (group != null && group.getTimedGeoFences() != null)
        {
            List<TimedGeoFence> timedGeoFences = group.getTimedGeoFences().stream().filter(f -> !f.getId().equals(fenceId)).collect(Collectors.toList());

            group.setTimedGeoFences(timedGeoFences);

            Group saved = groupRepository.save(group);

            return Utils.checkMongoSave(saved, fenceId.toHexString());
        }
        return ResponseEntity.ok(fenceId.toHexString());
    }

    @PostMapping("/groups/addgeofence")
    public ResponseEntity<String> addGeoFence(TimedGeoFence timedGeoFence) throws CustomException
    {
        Group group = groupRepository.findOne(timedGeoFence.getId());

        if (group != null)
        {
            ObjectId fenceId = ObjectId.get();

            List<TimedGeoFence> timedGeoFences = group.getTimedGeoFences();

            if (timedGeoFences == null)
                timedGeoFences = new ArrayList<>();

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
