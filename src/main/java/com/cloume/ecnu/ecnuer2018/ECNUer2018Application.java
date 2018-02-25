package com.cloume.ecnu.ecnuer2018;

import com.cloume.wx.WxCloumeService;
import com.mongodb.Mongo;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class ECNUer2018Application {


    public static void main(String[] args) {
        SpringApplication.run(ECNUer2018Application.class, args);
    }

    @Value("${spring.data.mongodb.database}")
    String dbName;

    @Autowired
    WxCloumeService wxCloumeService;

    @Bean
    WxMpService wxMpService() {
        WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
        config.setAppId(wxCloumeService.getAppId());
        config.setSecret(wxCloumeService.getSecret());
        config.setToken(wxCloumeService.getToken());
        config.setAesKey(wxCloumeService.getEncodingAESKey());

        WxMpService result = new WxMpServiceImpl();
        result.setWxMpConfigStorage(config);
        return result;
    }

    @Bean
    public MongoOperations mongoTemplate(Mongo mongo) {
        return new MongoTemplate(mongo, dbName);
    }
}
