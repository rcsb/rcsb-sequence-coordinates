package org.rcsb.collectors.sequence;

import org.rcsb.common.constants.MongoCollections;
import org.rcsb.mojave.CoreConstants;
import org.rcsb.utils.MongoStream;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;

public class SequenceCollector {

    public static Mono<String> getSequence(String sequenceId){
        return Mono.from(MongoStream.getMongoDatabase().getCollection(MongoCollections.COLL_SEQUENCE_COORDINATES_SEQUENCES)
                .aggregate(List.of(
                        match(eq(CoreConstants.SEQUENCE_ID, sequenceId)),
                        project(fields(excludeId()))
                    )
                )).map( d-> d.getString(CoreConstants.SEQUENCE));
    }

}
