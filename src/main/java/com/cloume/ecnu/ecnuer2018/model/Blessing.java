package com.cloume.ecnu.ecnuer2018.model;

import com.cloume.ecnu.ecnuer2018.repository.Received;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document(collection = "message")
public class Blessing {
    @Id
    String id;

    @Indexed
    Sender sender;

    /**
     * 用户填写的发布者昵称，可以不等于微信名
     */
    String author;
    String content;
    Long index = 1L;
    @DBRef
    Collection<MyUser> receivers;

    @Indexed
    Date timestamp = new Date();

    public Blessing() {
        this.receivers = new ArrayList<>();
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getAuthor() {
        return author;
    }

    public Collection<MyUser> getReceivers() {
        return receivers;
    }

    public void setReceiverAvatars(Collection<MyUser> receivers) {
        this.receivers = receivers;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    static public class Sender {
        public String openid,
                name,
                portrait;

        public Sender() {
        }

        public Sender(String openid, String name, String portrait) {
            this.name = name;
            this.openid = openid;
            this.portrait = portrait;
        }
    }
}