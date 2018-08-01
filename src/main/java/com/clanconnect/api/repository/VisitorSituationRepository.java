package com.clanconnect.api.repository;

import com.clanconnect.api.model.Visitor;
import com.clanconnect.api.model.VisitorSituation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorSituationRepository extends MongoRepository<VisitorSituation, ObjectId>
{
    VisitorSituation findByVisitor(Visitor visitor);


    List<VisitorSituation> findByVisitorIn(List<Visitor> visitors);
}
