/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.collectors.utils.AlignmentRangeIntersection;
import org.rcsb.collectors.utils.GroupFilterOperator;
import org.rcsb.collectors.utils.RangeIntersectionOperator;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import reactor.core.publisher.Flux;

import org.rcsb.utils.MongoStream;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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

    private final Supplier<Flux<Document>> documentSupplier;
    private Function<Flux<Document>,Flux<Document>> filterRange = Function.identity();
    private Function<Flux<Document>,Flux<Document>> filterTarget = Function.identity();

    private AlignmentsCollector(Supplier<Flux<Document>> documentSupplier){
        this.documentSupplier = documentSupplier;
    }

    public static AlignmentsCollector request(String queryId, SequenceReference from, SequenceReference to){
        return new AlignmentsCollector( () -> getAlignments(queryId, from, to) );
    }

    public static AlignmentsCollector request(String queryId, String targetId, SequenceReference from, SequenceReference to){
        return new AlignmentsCollector( () -> getAlignments(queryId, targetId, from, to) );
    }

    public static AlignmentsCollector request(String groupId, GroupReference group){
        return new AlignmentsCollector( () -> getAlignments(groupId, group));
    }

    public AlignmentsCollector range(List<Integer> range){
        RangeIntersectionOperator alignmentRangeIntersection = new RangeIntersectionOperator(range, new AlignmentRangeIntersection());
        this.filterRange = documentFlux -> documentFlux
                .filter(alignmentRangeIntersection::isConnected)
                .map(alignmentRangeIntersection::applyRange);
        return this;
    }

    public AlignmentsCollector filter(List<String> filter){
        GroupFilterOperator groupFilter = new GroupFilterOperator(filter);
        this.filterTarget = documentFlux -> documentFlux
                .filter(groupFilter::contains);
        return this;
    }

    public Flux<Document> get(){
        return this.filterRange.apply(this.filterTarget.apply(documentSupplier.get()));
    }

    private static Flux<Document> getAlignments(String queryId, SequenceReference from, SequenceReference to){
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

    private static Flux<Document> getAlignments(String queryId, String targetId, SequenceReference from, SequenceReference to){
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

    private static Flux<Document> getAlignments(String groupId, GroupReference group){
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
