/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.sequence;

import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.utils.MongoStream;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static org.rcsb.rcsbsequencecoordinates.collectors.map.MapCollector.getQueryIdMap;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class SequenceCollector {

    public static Mono<String> request(String sequenceId, SequenceReference reference){
        if(reference.equals(SequenceReference.PDB_INSTANCE))
            return getQueryIdMap(sequenceId, reference).flatMap(SequenceCollector::request).next();
        return request(sequenceId);
    }

    public static Mono<String> request(String sequenceId){
        return Mono.from(MongoStream.getMongoDatabase().getCollection(MongoCollections.COLL_SEQUENCE_COORDINATES_SEQUENCES)
                .aggregate(List.of(
                        match(eq(SequenceCoordinatesConstants.SEQUENCE_ID, sequenceId)),
                        project(fields(excludeId()))
                    )
                )).map( d-> d.getString(SequenceCoordinatesConstants.SEQUENCE));
    }


}
