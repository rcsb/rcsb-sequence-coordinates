/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.rcsbsequencecoordinates.collectors.sequence.SequenceCollector;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.rcsbsequencecoordinates.utils.ReactiveMongoResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsMongoHelper.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsReferenceHelper.equivalentReferences;

/**
 * @author : joan
 */
@Service
public class AlignmentLengthCollector {

    private final ReactiveMongoResource mongoResource;
    private final SequenceCollector sequenceCollector;

    public AlignmentLengthCollector(ReactiveMongoResource mongoResource) {
        this.mongoResource = mongoResource;
        this.sequenceCollector = new SequenceCollector(mongoResource);
    }

    public Mono<Integer> request(String queryId, SequenceReference from, SequenceReference to){
        if(equivalentReferences(from,to))
            return getIdentityLength(queryId);
        return getAlignmentDocument(
                getCollection(from, to),
                getIndex(from,to),
                queryId
        ).map(
                d->d.get(SequenceCoordinatesConstants.COVERAGE, Document.class).getInteger(getLengthAttribute(from, to))
        );
    }

    public Mono<Integer> request(String groupId, GroupReference group){
        if(group.equals(GroupReference.MATCHING_UNIPROT_ACCESSION))
            return request(groupId, SequenceReference.UNIPROT, SequenceReference.PDB_ENTITY);
        return getAlignmentDocument(
                getGroupCollection(),
                getGroupIndex(),
                groupId
        ).map(
                d->d.get(SequenceCoordinatesConstants.COVERAGE, Document.class).getInteger(getGroupLengthAttribute())
        );
    }

    private Mono<Document> getAlignmentDocument(String collection, String attribute, String id){
        List<Bson> aggregation = new ArrayList<>(List.of(
                match(eq(attribute, id)),
                alignmentLengthFields()
        ));
        return Mono.from(mongoResource.getDatabase().getCollection(collection).aggregate(aggregation).first());
    }

    private Mono<Integer> getIdentityLength(String queryId){
        return Mono.from(sequenceCollector.request(queryId).map(String::length));
    }

}
