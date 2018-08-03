package com.campaigns.api.repository;

import com.campaigns.api.model.Visitor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorRepository extends MongoRepository<Visitor, ObjectId>
{
    @Query("{beaconId:{$in:?0}}")
    List<Visitor> findByBeaconIdIn(List<String> beaconIds);

    Visitor findByBeaconId(String beaconIds);
}
