package com.cloume.ecnu.ecnuer2018.config;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {
    @Value("${spring.data.mongodb.host}") private String mongoHost;
    @Value("${spring.data.mongodb.host2:unavailable}") private String mongoHost2;
    @Value("${spring.data.mongodb.username:none}") private String mongoUsername;
    @Value("${spring.data.mongodb.password:none}") private String mongoPassword;
    @Value("${spring.data.mongodb.port:37017}") private int mongoPort;
    @Value("${spring.data.mongodb.database}") private String mongoDatabase;
    @Value("${spring.data.mongodb.isDebug:true}") private boolean isDebug;

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), getDatabaseName());
    }

    @Override
    public Mongo mongo() throws Exception {

        if (isDebug) {
            return new MongoClient(mongoHost, mongoPort);
        }

        // 构建主备数据库Seed列表
        List<ServerAddress> seedList = new ArrayList<ServerAddress>();
        ServerAddress seed1 = new ServerAddress(mongoHost, mongoPort);
        ServerAddress seed2 = new ServerAddress(mongoHost2, mongoPort);
        seedList.add(seed1);
        seedList.add(seed2);

        // 构建鉴权信息，账号验证数据库名与数据库名一致
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(MongoCredential.createScramSha1Credential(mongoUsername,
                getDatabaseName(), mongoPassword.toCharArray()));

        // 构建操作选项，requiredReplicaSetName属性外的选项根据自己的实际需求配置，默认参数满足大多数场景
        MongoClientOptions options = MongoClientOptions.builder()
                .serverSelectionTimeout(3000)
                .socketTimeout(2000)
                .connectionsPerHost(5)
                .build();

        return new MongoClient(seedList, credentials, options);
    }

}
