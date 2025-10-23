/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.rcsbsequencecoordinates.collectors.sequence.SequenceCollector;
import org.rcsb.rcsbsequencecoordinates.collectors.utils.AlignmentRangeIntersection;
import org.rcsb.rcsbsequencecoordinates.collectors.utils.RangeIntersectionOperator;
import org.rcsb.rcsbsequencecoordinates.collectors.map.MapCollector;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.rcsbsequencecoordinates.utils.ReactiveMongoResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsDocumentHelper.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsMongoHelper.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsReferenceHelper.equivalentReferences;

/**
 * @author : joan
 */
@Service
public class ProteinAlignmentsCollector implements AlignmentsCollector {

    private final ReactiveMongoResource mongoResource;
    private final SequenceCollector sequenceCollector;
    private final MapCollector mapCollector;
    private Supplier<Flux<Document>> documentSupplier;
    private Function<Flux<Document>,Flux<Document>> filterRange = Function.identity();
    private List<String> filterTarget = List.of();
    private List<Integer> page = List.of();
    private Bson sortAggregator;
    private int max = 0;

    @Autowired
    public ProteinAlignmentsCollector(ReactiveMongoResource mongoResource) {
        this.mongoResource = mongoResource;
        this.sequenceCollector = new SequenceCollector(mongoResource);
        this.mapCollector = new MapCollector(mongoResource);
    }

    public static AlignmentsCollector request(ReactiveMongoResource mongoResource, String queryId, SequenceReference from, SequenceReference to) {
        ProteinAlignmentsCollector pac =  new ProteinAlignmentsCollector(mongoResource);
        pac.sortAggregator = getSortFields(from,to);
        pac.documentSupplier = () -> pac.getAlignments(queryId, from, to);
        return pac;
    }

    public static AlignmentsCollector request(ReactiveMongoResource mongoResource, String groupId, GroupReference group) {
        ProteinAlignmentsCollector pac =  new ProteinAlignmentsCollector(mongoResource);
        pac.sortAggregator = getGroupSortFields();
        pac.documentSupplier = () -> pac.getAlignments(groupId, group);
        return pac;
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
        if(first == null && offset == null)
            return this;
        page = List.of(Objects.requireNonNullElse(offset, 0), Objects.requireNonNullElse(first, 1));
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
        return mapCollector.getQueryIdMap(queryId, from).flatMap(
                q -> alignmentsCollector(from, to).apply(q, from, to)
        ).map(
                d -> targetIdSelector(from, to).apply(d)
        ).flatMap(
                d -> getTargetIdMap(d, queryId, from, to)
        );
    }

    private Flux<Document> getTargetIdMap( Document alignment,String queryId, SequenceReference from, SequenceReference to){
        return mapCollector.getTargetIdMap(alignment.getString(getTargetIndex()), to)
                .filter(t->!(to.equals(SequenceReference.PDB_INSTANCE) && from.equals(SequenceReference.PDB_INSTANCE) && !queryId.equals(t)))
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

    private Flux<Document> getIdentityAlignment(String queryId){
        return Flux.from(sequenceCollector.request(queryId).map(sequence->identityAlignment(queryId, sequence.length())));
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
        aggregation.addAll(aggregationFilter(attribute));
        aggregation.addAll(limitRequest());
        aggregation.addAll(aggregationPage());
        return Flux.from(mongoResource.getDatabase().getCollection(collection).aggregate(aggregation));
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
            return List.of(match(in(getAltIndex(attribute), filterTarget)));
        return List.of();
    }

    private List<Bson> limitRequest(){
        if(max > 0)
            return List.of(limit(max));
        return List.of();
    }

}
