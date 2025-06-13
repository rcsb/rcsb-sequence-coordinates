/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.component;

import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;



/**
 * @author : joan
 */
@Component
public class MyReactiveHealthIndicator implements ReactiveHealthIndicator {

    private final MongoClient mongoClient;
    private final MongoProperties mongoProperties;

    @Autowired
    public MyReactiveHealthIndicator(MongoClient mongoClient, MongoProperties mongoProperties) {
        this.mongoClient = mongoClient;
        this.mongoProperties = mongoProperties;
    }

    @Override
    public Mono<Health> health() {
        return doHealthCheck()
                .onErrorResume((exception) -> Mono.just(new Health.Builder().down(exception).build()));
    }

    private Mono<Health> doHealthCheck() {
        return Mono.from(mongoClient.getDatabase(mongoProperties.getDatabase()).runCommand(new Document("ping", 1))).map(
                pinDoc -> {
                    if(pinDoc.getDouble("ok").equals(1.0))
                        return Health.up().build();
                    return Health.down().build();
                }
        );
    }

}
