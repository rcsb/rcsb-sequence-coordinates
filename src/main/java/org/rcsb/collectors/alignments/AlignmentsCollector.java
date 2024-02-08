/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.collectors.utils.AlignmentRangeIntersection;
import org.rcsb.collectors.utils.GroupFilter;
import org.rcsb.collectors.utils.RangeIntersection;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import reactor.core.publisher.Flux;

import org.rcsb.utils.MongoStream;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.*;
import static org.rcsb.collectors.alignments.AlignmentsHelper.*;
import static org.rcsb.collectors.map.MapCollector.getQueryIdMap;
import static org.rcsb.collectors.map.MapCollector.getTargetIdMap;
import static org.rcsb.collectors.sequence.SequenceCollector.getSequence;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class AlignmentsCollector {

    public static Flux<Document> getAlignments(String queryId, SequenceReference from, SequenceReference to, List<Integer> range) {
        RangeIntersection alignmentRangeIntersection = new RangeIntersection(range, new AlignmentRangeIntersection());
        if(alignmentRangeIntersection.isEmptyRange())
            return getAlignments(queryId, from, to);

        return getAlignments(queryId, from, to)
                .filter(alignmentRangeIntersection::isConnected)
                .map(alignmentRangeIntersection::applyRange);
    }

    public static Flux<Document> getAlignments(String queryId, SequenceReference from, SequenceReference to){
        return getQueryIdMap(queryId, from).flatMap(
                q -> alignmentsCollector(from, to).apply(q, from, to)
        ).map(
                d -> targetIdSelector(from, to).apply(d)
        ).flatMap(
                d-> getTargetIdMap(d.getString(getTargetIndex()), to).map(
                        t -> targetIdSubstitutor(to).apply(t, d)
                )
        );
    }

    public static Flux<Document> getAlignments(String queryId, String targetId, SequenceReference from, SequenceReference to){
        return getQueryIdMap(queryId, from).flatMap(
                q-> getQueryIdMap(targetId, to).flatMap(t -> getAlignmentDocuments(q, t, from, to))
        ).map(
                d -> targetIdSelector(from, to).apply(d)
        ).flatMap(
                d-> getTargetIdMap(d.getString(getTargetIndex()), to).map(
                        t -> targetIdSubstitutor(to).apply(t, d)
                )
        );
    }

    public static Flux<Document> getAlignments(String groupId, GroupReference group, List<String> filter){
        if(filter == null || filter.isEmpty())
            return getAlignments(groupId, group);
        GroupFilter groupFilter = new GroupFilter(filter);
        return getAlignments(groupId, group)
                .filter(groupFilter::contains);
    }

    public static Flux<Document> getAlignments(String groupId, GroupReference group){
        if(group.equals(GroupReference.MATCHING_UNIPROT_ACCESSION))
            return getAlignments(groupId, SequenceReference.UNIPROT, SequenceReference.PDB_ENTITY);
        return getAlignmentDocuments(groupId);
    }

    private static AlignmentBuilder alignmentsCollector(SequenceReference from, SequenceReference to){
        if(equivalentReferences(from,to))
            return (q, f, t) -> getIdentityAlignment(q);
        return AlignmentsCollector::getAlignmentDocuments;
    }

    private static Flux<Document> getIdentityAlignment(String queryId){
        return Flux.from(getSequence(queryId).map(sequence->identityAlignment(queryId, sequence.length())));
    }

    private static Flux<Document> getAlignmentDocuments(String queryId, SequenceReference from, SequenceReference to){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getCollection(from, to)).aggregate(List.of(
                match(eq(getIndex(from, to), queryId)),
                sort(orderBy(
                        ascending(getSortFields(from, to).get(0)),
                        descending(getSortFields(from, to).get(1))
                )),
                alignmentFields()
        )));
    }

    private static Flux<Document> getAlignmentDocuments(String groupId){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getGroupCollection()).aggregate(List.of(
                match(eq(getGroupIndex(), groupId)),
                sort(orderBy(
                        ascending(getGroupSortFields().get(0)),
                        descending(getGroupSortFields().get(1))
                )),
                alignmentFields()
        )));
    }

    private static Flux<Document> getAlignmentDocuments(String queryId, String targetId, SequenceReference from, SequenceReference to){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getCollection(from, to)).aggregate(List.of(
                match(eq(getIndex(from, to), queryId)),
                match(eq(getAltIndex(from, to), targetId)),
                sort(orderBy(
                        ascending(getSortFields(from, to).get(0)),
                        descending(getSortFields(from, to).get(1))
                )),
                alignmentFields()
        )).first());
    }

}
