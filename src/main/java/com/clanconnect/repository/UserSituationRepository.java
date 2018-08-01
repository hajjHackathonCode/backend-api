package com.clanconnect.repository;

import com.clanconnect.model.UserSituation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSituationRepository extends MongoRepository<UserSituation, ObjectId>
{

}
