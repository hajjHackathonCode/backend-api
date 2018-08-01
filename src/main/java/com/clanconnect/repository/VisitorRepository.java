package com.clanconnect.repository;

import com.clanconnect.model.Visitor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorRepository extends MongoRepository<Visitor, ObjectId>
{
    List<Visitor> findByBeaconIdIn(List<String> beaconIds);
}
