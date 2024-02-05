/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.utils;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class MongoStream {

    private static final Logger logger = LoggerFactory.getLogger(MongoStream.class);
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    public static MongoDatabase getMongoDatabase(){
        if(mongoDatabase != null)
            return mongoDatabase;

        String dbName = Parameters.getPropertiesReader().loadStringField("aw.mongodb.db.name", null);
        mongoDatabase = MongoStream.getMongoClient().getDatabase(dbName);
        return mongoDatabase;
    }

    private static MongoClient getMongoClient() {

        if (mongoClient != null)
            return mongoClient;

        String mongoDbUri = Parameters.getPropertiesReader().loadStringField("aw.mongodb.uri", null);
        ConnectionString uri = new ConnectionString(mongoDbUri);

        logger.info("Created new MongoDB client with uri: {}", mongoDbUri);
        mongoClient = MongoClients.create(uri);
        return mongoClient;

    }


}
