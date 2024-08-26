/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.collectors.sequence.SequenceCollector;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.utils.MongoStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static org.rcsb.collectors.alignments.AlignmentsMongoHelper.*;
import static org.rcsb.collectors.alignments.AlignmentsReferenceHelper.equivalentReferences;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/13/24, Tuesday
 **/
public class AlignmentLengthCollector {

    public static Mono<Integer> request(String queryId, SequenceReference from, SequenceReference to){
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

    public static Mono<Integer> request(String groupId, GroupReference group){
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

    private static Mono<Document> getAlignmentDocument(String collection, String attribute, String id){
        List<Bson> aggregation = new ArrayList<>(List.of(
                match(eq(attribute, id)),
                alignmentLengthFields()
        ));
        return Mono.from(MongoStream.getMongoDatabase().getCollection(collection).aggregate(aggregation).first());
    }

    private static Mono<Integer> getIdentityLength(String queryId){
        return Mono.from(SequenceCollector.request(queryId).map(String::length));
    }

}
