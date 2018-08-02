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
    VisitorSituation findByVisitor(Visitor visitor);

    @Query("{visitor: {$in: ?0}}")
    List<VisitorSituation> findByVisitorIn(List<Visitor> visitors);

    @Query("{visitor.groupId: ?0}")
    List<VisitorSituation> findByGroupId(ObjectId groupId);

    List<VisitorSituation> findAllBySituationStatus(SituationStatus situationStatus);
}
