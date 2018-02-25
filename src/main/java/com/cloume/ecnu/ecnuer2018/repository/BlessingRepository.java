package com.cloume.ecnu.ecnuer2018.repository;

import com.cloume.ecnu.ecnuer2018.model.Blessing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BlessingRepository extends MongoRepository<Blessing, String> {

    @Query("{ 'sender.openid': ?0 }")
    List<Blessing> findBySender(String openidOfSender, Pageable pageable);

    @Query("{ 'receivers.$id': ?0 }")
    List<Blessing> findByReceiver(String openidOfReceiver, Pageable pageable);
}
