package com.cloume.ecnu.ecnuer2018.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReceivedRepository extends MongoRepository<Received, String> {
    @Query(value = "{ 'blessing': ?1, 'receiver.openid': ?0 }", exists = true)
    boolean existsByReceiverAndBlessing(String openidOfReceiver, String idOfBlessing);
}
