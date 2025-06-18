package org.rcsb.rcsbsequencecoordinates.utils;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;

public record ReactiveMongoResource(MongoClient mongoClient, String dbName) {

    public MongoDatabase getDatabase() {
        return mongoClient.getDatabase(dbName);
    }
}
