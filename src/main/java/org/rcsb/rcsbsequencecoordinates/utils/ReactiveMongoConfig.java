package org.rcsb.rcsbsequencecoordinates.utils;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveMongoConfig {

//    @Bean
//    public MongoClient reactiveMongoClient(MongoProperties mongoProperties) {
//        return MongoClients.create(mongoProperties.getUri());
//    }

    @Bean
    public MongoConnectionDetails mongoConnectionDetails(MongoProperties mongoProperties) {
        return () -> new ConnectionString(mongoProperties.getUri());
    }

    @Bean
    public MongoClient reactiveMongoClient(MongoConnectionDetails mongoConnectionDetails) {
        return MongoClients.create(mongoConnectionDetails.getConnectionString());
    }
}