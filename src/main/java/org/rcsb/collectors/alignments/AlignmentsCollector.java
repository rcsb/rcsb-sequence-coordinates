/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.collectors.map.MapCollector;
import org.rcsb.collectors.sequence.SequenceCollector;
import org.rcsb.collectors.utils.AlignmentRangeIntersection;
import org.rcsb.collectors.utils.RangeIntersectionOperator;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import reactor.core.publisher.Flux;

import org.rcsb.utils.MongoStream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;
import static org.rcsb.collectors.alignments.AlignmentsHelper.*;
import static org.rcsb.collectors.map.MapCollector.getQueryIdMap;
import static org.rcsb.collectors.map.MapCollector.getTargetIdMap;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class AlignmentsCollector {

    private Supplier<Flux<Document>> documentSupplier;
    private Function<Flux<Document>,Flux<Document>> filterRange = Function.identity();
    private List<String> filterTarget = List.of();
    private List<Integer> page = List.of();

    private AlignmentsCollector(){
    }

    public static AlignmentsCollector build(){
        return new AlignmentsCollector();
    }

    public AlignmentsCollector request(String queryId, SequenceReference from, SequenceReference to){
        documentSupplier = () -> getAlignments(queryId, from, to);
        return this;
    }

    public AlignmentsCollector request(String groupId, GroupReference group){
        documentSupplier = () -> getAlignments(groupId, group);
        return this;
    }

    public AlignmentsCollector range(List<Integer> range){
        RangeIntersectionOperator alignmentRangeIntersection = new RangeIntersectionOperator(range, new AlignmentRangeIntersection());
        this.filterRange = documentFlux -> documentFlux
                .filter(alignmentRangeIntersection::isConnected)
                .map(alignmentRangeIntersection::applyRange);
        return this;
    }

    public AlignmentsCollector filter(List<String> filter){
        if(filter != null)
            this.filterTarget = filter;
        return this;
    }

    public AlignmentsCollector page(Integer first, Integer offset){
        if(first != null && offset != null)
            page = List.of(offset, first);
        return this;
    }

    public Flux<Document> get(){
        return documentSupplier.get()
                .transform(filterRange);
    }

    public static Flux<String> mapIds(SequenceReference from, SequenceReference to, List<String> ids){
        if(equivalentReferences(from,to))
            return MapCollector.mapEquivalentReferences(from, to, ids);
        return getMapDocuments(
                getCollection(from, to),
                getIndex(from, to),
                ids
        ).map(map -> map.getString(getAltIndex(from, to)));
    }

    private Flux<Document> getAlignments(String queryId, SequenceReference from, SequenceReference to){
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
                queryId,
                getSortFields(from,to)
        );
    }

    private Flux<Document> getAlignmentDocuments(String groupId){
        return getAlignmentDocuments(
                getGroupCollection(),
                getGroupIndex(),
                groupId,
                getGroupSortFields()
        );
    }

    private Flux<Document> getAlignmentDocuments(String collection, String attribute, String id, List<String> sortFields){
        List<Bson> aggregation = new ArrayList<>(List.of(
                match(eq(attribute, id)),
                sort(orderBy(
                        ascending(sortFields.get(0)),
                        descending(sortFields.get(1))
                )),
                alignmentFields()
        ));
        aggregation.addAll(aggregationPage());
        aggregation.addAll(aggregationFilter(attribute));
        return Flux.from(MongoStream.getMongoDatabase().getCollection(collection).aggregate(aggregation));
    }

    private static Flux<Document> getMapDocuments(String collection, String attribute, List<String> ids){
        List<Bson> aggregation = List.of(
                match(or(ids.stream().map(id->eq(attribute, id)).toList())),
                mapFields()
        );
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

}
