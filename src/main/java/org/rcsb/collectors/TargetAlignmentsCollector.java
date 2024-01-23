package org.rcsb.collectors;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.response.TargetAlignment;
import org.rcsb.utils.CustomObjectMapper;
import reactor.core.publisher.Flux;

import org.rcsb.utils.MongoStream;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static org.rcsb.collectors.TargetAlignmentsHelper.getDocumentMap;

public class TargetAlignmentsCollector {

    public static Flux<TargetAlignment> getAlignments(String queryId, SequenceReference from, SequenceReference to){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(TargetAlignmentsHelper.getCollection(from, to)).aggregate(List.of(
                match(eq(TargetAlignmentsHelper.getIndex(from, to), queryId)),
                TargetAlignmentsHelper.alignmentFields()
        ))).map(
                d -> getDocumentMap(from, to).apply(d)
        ).map(
                d -> CustomObjectMapper.getDeserializationMapper().convertValue(d,TargetAlignment.class)
        );
    }

    public static Flux<TargetAlignment> getAlignments(String groupId, GroupReference group){
        if(group.equals(GroupReference.MATCHING_UNIPROT_ACCESSION))
            return getAlignments(groupId, SequenceReference.UNIPROT, SequenceReference.PDB_ENTITY);

        return Flux.from(MongoStream.getMongoDatabase().getCollection(TargetAlignmentsHelper.getGroupCollection()).aggregate(List.of(
                match(eq(TargetAlignmentsHelper.getGroupIndex(), groupId)),
                TargetAlignmentsHelper.alignmentFields()
        ))).map(
                d-> CustomObjectMapper.getDeserializationMapper().convertValue(d,TargetAlignment.class)
        );
    }

}
