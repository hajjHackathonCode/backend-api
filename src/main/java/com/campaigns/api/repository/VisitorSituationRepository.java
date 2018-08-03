package com.campaigns.api.repository;

import com.campaigns.api.model.SituationStatus;
import com.campaigns.api.model.Visitor;
import com.campaigns.api.model.VisitorSituation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorSituationRepository extends MongoRepository<VisitorSituation, ObjectId>
{
    @Query("{visitor.beaconId: ?0}")
    VisitorSituation findByVisitorBeaconId(String beaconId);

    @Query("{visitor: {$in: ?0}}")
    List<VisitorSituation> findByVisitorIn(List<Visitor> visitors);

    @Query("{visitor.beaconId: {$in: ?0}}")
    List<VisitorSituation> findByVisitorBeaconIdIn(List<String> visitorsBeaconId);

    @Query("{visitor.groupId: ?0}")
    List<VisitorSituation> findByGroupId(ObjectId groupId);

    @Query("{visitor.groupId: ?0, situationStatus: ?1}")
    List<VisitorSituation> findByGroupIdAndSituationStatus(ObjectId groupId, SituationStatus situationStatus);

    List<VisitorSituation> findAllBySituationStatus(SituationStatus situationStatus);
}
