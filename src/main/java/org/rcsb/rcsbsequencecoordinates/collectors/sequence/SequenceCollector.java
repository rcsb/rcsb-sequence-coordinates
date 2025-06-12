/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.sequence;

import com.mongodb.reactivestreams.client.MongoClient;
import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.rcsbsequencecoordinates.collectors.map.MapCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;

/**
 * @author : joan
 */
@Service
public class SequenceCollector {

    private final MongoClient mongoClient;
    private final MongoProperties mongoProperties;
    private final MapCollector mapCollector;

    @Autowired
    public  SequenceCollector(MongoClient mongoClient, MongoProperties mongoProperties) {
        this.mongoClient = mongoClient;
        this.mongoProperties = mongoProperties;
        this.mapCollector = new MapCollector(mongoClient, mongoProperties);
    }

    public Mono<String> request(String sequenceId, SequenceReference reference){
        if(reference.equals(SequenceReference.PDB_INSTANCE))
            return mapCollector.getQueryIdMap(sequenceId, reference).flatMap(this::request).next();
        return request(sequenceId);
    }

    public Mono<String> request(String sequenceId) {
        return Mono.from(mongoClient.getDatabase(mongoProperties.getDatabase()).getCollection(MongoCollections.COLL_SEQUENCE_COORDINATES_SEQUENCES)
                .aggregate(List.of(
                        match(eq(SequenceCoordinatesConstants.SEQUENCE_ID, sequenceId)),
                        project(fields(excludeId()))
                    )
                )).map( d-> d.getString(SequenceCoordinatesConstants.SEQUENCE));
    }


}
