package com.cloume.ecnu.ecnuer2018.repository;

import com.cloume.ecnu.ecnuer2018.model.Blessing;
import com.cloume.ecnu.ecnuer2018.model.MyUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "received")
public class Received {
    @Id
    String id;

    // blessing id
    String blessing;

    // receiver openid
    Blessing.Sender receiver;

    @Indexed
    Date timestamp = new Date();

    public Received() {
    }

    public Received(String idOfblessing, MyUser viewer) {
        this.blessing = idOfblessing;
        this.receiver = new Blessing.Sender(
                viewer.getOpenId(),
                StringUtils.isEmpty(viewer.getRealname()) ? viewer.getNickname() : viewer.getRealname(),
                viewer.getPortrait()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlessing() {
        return blessing;
    }

    public void setBlessing(String blessing) {
        this.blessing = blessing;
    }

    public Blessing.Sender getReceiver() {
        return receiver;
    }

    public void setReceiver(Blessing.Sender receiver) {
        this.receiver = receiver;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
