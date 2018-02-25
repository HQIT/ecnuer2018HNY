package com.cloume.ecnu.ecnuer2018.service;

import com.cloume.ecnu.ecnuer2018.model.MyUser;


public interface IMessageService {

    void setBlessingReceived(String msgId, MyUser receivedUser);
}
