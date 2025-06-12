package org.rcsb.rcsbsequencecoordinates.utils;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class ReactiveMongoConfig {

    @Bean
    public MongoConnectionDetails mongoConnectionDetails(MongoProperties props) {
        String uri = String.format("mongodb://%s:%s@%s:%d",
                props.getUsername(),
                URLEncoder.encode(new String(props.getPassword()), StandardCharsets.UTF_8),
                props.getHost(),
                props.getPort());
        return () -> new ConnectionString(uri);
    }

    @Bean
    public MongoClient reactiveMongoClient(MongoConnectionDetails mongoConnectionDetails) {
        return MongoClients.create(mongoConnectionDetails.getConnectionString());
    }

    @Bean
    public ReactiveMongoResource reactiveMongoResource(MongoClient mongoClient, MongoProperties mongoProperties) {
        return new ReactiveMongoResource(mongoClient, mongoProperties.getDatabase());
    }
}