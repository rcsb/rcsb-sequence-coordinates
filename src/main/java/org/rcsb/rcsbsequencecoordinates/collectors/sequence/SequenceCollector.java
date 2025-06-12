/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.sequence;

import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.rcsbsequencecoordinates.collectors.map.MapCollector;
import org.rcsb.rcsbsequencecoordinates.utils.ReactiveMongoResource;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ReactiveMongoResource mongoResource;
    private final MapCollector mapCollector;

    @Autowired
    public  SequenceCollector(ReactiveMongoResource mongoResource) {
        this.mongoResource = mongoResource;
        this.mapCollector = new MapCollector(mongoResource);
    }

    public Mono<String> request(String sequenceId, SequenceReference reference){
        if(reference.equals(SequenceReference.PDB_INSTANCE))
            return mapCollector.getQueryIdMap(sequenceId, reference).flatMap(this::request).next();
        return request(sequenceId);
    }

    public Mono<String> request(String sequenceId) {
        return Mono.from(mongoResource.getDatabase().getCollection(MongoCollections.COLL_SEQUENCE_COORDINATES_SEQUENCES)
                .aggregate(List.of(
                        match(eq(SequenceCoordinatesConstants.SEQUENCE_ID, sequenceId)),
                        project(fields(excludeId()))
                    )
                )).map( d-> d.getString(SequenceCoordinatesConstants.SEQUENCE));
    }


}
