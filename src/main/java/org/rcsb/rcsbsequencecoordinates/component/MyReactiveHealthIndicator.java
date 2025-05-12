/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.component;

import org.bson.Document;
import org.rcsb.utils.MongoStream;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;



/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 8/26/24, Monday
 **/

@Component
public class MyReactiveHealthIndicator implements ReactiveHealthIndicator {

    @Override
    public Mono<Health> health() {
        return doHealthCheck()
                .onErrorResume((exception) -> Mono.just(new Health.Builder().down(exception).build()));
    }

    private Mono<Health> doHealthCheck() {
        return Mono.from(MongoStream.getMongoDatabase().runCommand(new Document("ping", 1))).map(
                pinDoc -> {
                    if(pinDoc.getDouble("ok").equals(1.0))
                        return Health.up().build();
                    return Health.down().build();
                }
        );
    }

}
