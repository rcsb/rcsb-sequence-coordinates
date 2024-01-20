package org.rcsb.collectors;

import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.response.TargetAlignment;
import org.rcsb.mojave.CoreConstants;
import org.rcsb.utils.CustomObjectMapper;
import reactor.core.publisher.Flux;

import org.rcsb.utils.MongoStream;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

public class TargetAlignmentsCollector {
    public static Flux<TargetAlignment> getAlignments(String queryId, SequenceReference from, SequenceReference to){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(TargetAlignmentsHelper.getCollection(from, to)).aggregate(List.of(
                match(eq(TargetAlignmentsHelper.getIndex(from, to), queryId)),
                project(fields(
                        include(CoreConstants.TARGET_ID),
                        include(CoreConstants.ALIGNED_REGIONS),
                        include(CoreConstants.SCORES),
                        excludeId()
                ))
        ))).map(
                d-> CustomObjectMapper.getDeserializationMapper().convertValue(d,TargetAlignment.class)
        );
    }

}
