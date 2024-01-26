package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import reactor.core.publisher.Flux;

import org.rcsb.utils.MongoStream;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.*;
import static org.rcsb.collectors.alignments.TargetAlignmentsHelper.*;


public class TargetAlignmentsCollector {

    public static Flux<Document> getAlignments(String queryId, SequenceReference from, SequenceReference to){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getCollection(from, to)).aggregate(List.of(
                match(eq(getIndex(from, to), queryId)),
                sort(orderBy(
                        ascending(getSortFields(from, to).get(0)),
                        descending(getSortFields(from, to).get(1))
                )),
                alignmentFields()
        ))).map(
                d -> getDocumentMap(from, to).apply(d)
        );
    }

    public static Flux<Document> getAlignments(String groupId, GroupReference group){
        if(group.equals(GroupReference.MATCHING_UNIPROT_ACCESSION))
            return getAlignments(groupId, SequenceReference.UNIPROT, SequenceReference.PDB_ENTITY);

        return Flux.from(MongoStream.getMongoDatabase().getCollection(getGroupCollection()).aggregate(List.of(
                match(eq(getGroupIndex(), groupId)),
                sort(orderBy(
                        ascending(getGroupSortFields().get(0)),
                        descending(getGroupSortFields().get(1))
                )),
                alignmentFields()
        )));
    }

}
