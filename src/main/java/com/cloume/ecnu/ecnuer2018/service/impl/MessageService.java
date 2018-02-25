package com.cloume.ecnu.ecnuer2018.service.impl;

import com.cloume.ecnu.ecnuer2018.model.Blessing;
import com.cloume.ecnu.ecnuer2018.model.MyUser;
import com.cloume.ecnu.ecnuer2018.repository.BlessingRepository;
import com.cloume.ecnu.ecnuer2018.repository.Received;
import com.cloume.ecnu.ecnuer2018.repository.ReceivedRepository;
import com.cloume.ecnu.ecnuer2018.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MessageService implements IMessageService {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    BlessingRepository blessingRepository;
    @Autowired
    ReceivedRepository receivedRepository;

    @Override
    public void setBlessingReceived(String msgId, MyUser receivedUser) {
        if (!StringUtils.isEmpty(msgId) && receivedUser != null) {
            Blessing blessing = blessingRepository.findOne(msgId);
            if (blessing != null && !blessing.getSender().openid.equals(receivedUser.getOpenId())) {
                boolean added = false;
                for (MyUser user: blessing.getReceivers()) {
                    if (user.getOpenId().equals(receivedUser.getOpenId())) {
                        added = true;
                    }
                }
                if (!added) {
                    blessing.getReceivers().add(receivedUser);
                    blessingRepository.save(blessing);
                }
            }
        }
    }

}
