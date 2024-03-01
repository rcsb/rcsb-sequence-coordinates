/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.collectors.sequence.SequenceCollector;
import org.rcsb.collectors.utils.AlignmentRangeIntersection;
import org.rcsb.collectors.utils.RangeIntersectionOperator;
import org.rcsb.collectors.map.MapCollector;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.springframework.data.mongodb.core.index.IndexDirection;
import reactor.core.publisher.Flux;

import org.rcsb.utils.MongoStream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static org.rcsb.collectors.alignments.AlignmentsDocumentHelper.*;
import static org.rcsb.collectors.alignments.AlignmentsMongoHelper.*;
import static org.rcsb.collectors.alignments.AlignmentsReferenceHelper.equivalentReferences;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class ProteinAlignmentsCollector implements AlignmentsCollector {

    private final Supplier<Flux<Document>> documentSupplier;
    private Function<Flux<Document>,Flux<Document>> filterRange = Function.identity();
    private List<String> filterTarget = List.of();
    private List<Integer> page = List.of();
    private Bson sortAggregator;
    private int max = 0;

    private ProteinAlignmentsCollector(String queryId, SequenceReference from, SequenceReference to){
        sortAggregator = getSortFields(from,to);
        documentSupplier = () -> getAlignments(queryId, from, to);
    }

    private ProteinAlignmentsCollector(String groupId, GroupReference group){
        sortAggregator = getGroupSortFields();
        documentSupplier = () -> getAlignments(groupId, group);
    }

    public static AlignmentsCollector request(String queryId, SequenceReference from, SequenceReference to){
        return new ProteinAlignmentsCollector(queryId, from, to);
    }

    public static AlignmentsCollector request(String groupId, GroupReference group){
        return new ProteinAlignmentsCollector(groupId, group);
    }

    @Override
    public AlignmentsCollector range(List<Integer> range){
        RangeIntersectionOperator alignmentRangeIntersection = new RangeIntersectionOperator(range, new AlignmentRangeIntersection());
        this.filterRange = documentFlux -> documentFlux
                .filter(alignmentRangeIntersection::isConnected)
                .map(alignmentRangeIntersection::applyRange);
        return this;
    }

    @Override
    public AlignmentsCollector filter(List<String> filter){
        if(filter != null)
            this.filterTarget = filter;
        return this;
    }

    @Override
    public AlignmentsCollector page(Integer first, Integer offset){
        if(first != null && offset != null)
            page = List.of(offset, first);
        return this;
    }

    @Override
    public AlignmentsCollector sort(String field, IndexDirection direction) {
        sortAggregator = sortAggregator(field, direction);
        return this;
    }

    @Override
    public AlignmentsCollector max(int n) {
        max = n;
        return this;
    }

    public Flux<Document> get(){
        return documentSupplier
                .get()
                .transform(filterRange);
    }

    private Flux<Document> getAlignments(String queryId, SequenceReference from, SequenceReference to){
        return MapCollector.getQueryIdMap(queryId, from).flatMap(
                q -> alignmentsCollector(from, to).apply(q, from, to)
        ).map(
                d -> targetIdSelector(from, to).apply(d)
        ).flatMap(
                d -> getTargetIdMap(d, to)
        );
    }

    private Flux<Document> getTargetIdMap( Document alignment, SequenceReference to){
        return MapCollector.getTargetIdMap(alignment.getString(getTargetIndex()), to)
                .map(t -> targetIdSubstitutor(to).apply(t,alignment));
    }

    private Flux<Document> getAlignments(String groupId, GroupReference group){
        if(group.equals(GroupReference.MATCHING_UNIPROT_ACCESSION))
            return getAlignments(groupId, SequenceReference.UNIPROT, SequenceReference.PDB_ENTITY);
        return getAlignmentDocuments(groupId);
    }

    private AlignmentBuilder alignmentsCollector(SequenceReference from, SequenceReference to){
        if(equivalentReferences(from,to))
            return (q, f, t) -> getIdentityAlignment(q);
        return this::getAlignmentDocuments;
    }

    private static Flux<Document> getIdentityAlignment(String queryId){
        return Flux.from(SequenceCollector.request(queryId).map(sequence->identityAlignment(queryId, sequence.length())));
    }

    private Flux<Document> getAlignmentDocuments(String queryId, SequenceReference from, SequenceReference to){
        return getAlignmentDocuments(
                getCollection(from, to),
                getIndex(from,to),
                queryId
        );
    }

    private Flux<Document> getAlignmentDocuments(String groupId){
        return getAlignmentDocuments(
                getGroupCollection(),
                getGroupIndex(),
                groupId
        );
    }

    private Flux<Document> getAlignmentDocuments(String collection, String attribute, String id){
        List<Bson> aggregation = new ArrayList<>(List.of(
                match(eq(attribute, id)),
                sortAggregator,
                alignmentFields()
        ));
        aggregation.addAll(aggregationPage());
        aggregation.addAll(aggregationFilter(attribute));
        aggregation.addAll(limitRequest());
        return Flux.from(MongoStream.getMongoDatabase().getCollection(collection).aggregate(aggregation));
    }

    private List<Bson> aggregationPage(){
        if(page.size() == 2)
            return List.of(
                    skip(page.get(0)),
                    limit(page.get(1))
            );
        return List.of();
    }

    private List<Bson> aggregationFilter(String attribute){
        if(!filterTarget.isEmpty())
            return List.of(match(or(filterTarget.stream().map(
                    targetId -> eq(getAltIndex(attribute), targetId)
            ).toList())));
        return List.of();
    }

    private List<Bson> limitRequest(){
        if(max > 0)
            return List.of(limit(max));
        return List.of();
    }

}
