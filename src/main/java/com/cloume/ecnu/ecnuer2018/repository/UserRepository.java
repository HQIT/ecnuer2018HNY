package com.cloume.ecnu.ecnuer2018.repository;

import com.cloume.ecnu.ecnuer2018.model.MyUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<MyUser, String> {
    @Query(value = "{ 'openId': ?0 }", exists = true)
    boolean existsByOpenId(String openId);
}
