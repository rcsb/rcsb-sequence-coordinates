/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.annotations;

import org.bson.Document;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.CoreConstants;
import org.rcsb.utils.MongoStream;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.rcsb.collectors.alignments.AlignmentsCollector.getAlignments;
import static org.rcsb.collectors.annotations.AnnotationsHelper.*;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class AnnotationsCollector {

    public static Flux<Document> getAnnotations(String queryId, SequenceReference sequenceReference, List<AnnotationReference> annotationReferences) {
        return Flux.fromIterable(annotationReferences).flatMap(annotationReference -> getAnnotations(queryId, sequenceReference, annotationReference));
    }

    public static Flux<Document> getAnnotations(String groupId, GroupReference groupReference, List<AnnotationReference> annotationReferences){
        if(groupReference.equals(GroupReference.MATCHING_UNIPROT_ACCESSION))
            return getAnnotations(groupId, SequenceReference.UNIPROT, annotationReferences);
        return Flux.fromIterable(annotationReferences).flatMap( annotationReference -> getAnnotations(groupId, groupReference, annotationReference));
    }

    private static Flux<Document> getAnnotations(String groupId, GroupReference groupReference, AnnotationReference annotationReference) {
        return getAlignments(
                groupId,
                groupReference
        ).flatMap(
                alignment -> getAnnotations(alignment.getString(CoreConstants.TARGET_ID), groupReference.toSequenceReference(), annotationReference)
        );
    }

    private static Flux<Document> getAnnotations(String queryId, SequenceReference sequenceReference, AnnotationReference annotationReference) {
        return getAlignments(
                queryId,
                sequenceReference,
                annotationReference.toSequenceReference()
        ).flatMap(
                alignment -> Flux.from(
                        MongoStream.getMongoDatabase().getCollection(getCollection(annotationReference)).aggregate(
                                getAggregation(alignment.getString(CoreConstants.TARGET_ID), annotationReference)
                        )
                ).map(
                        annotations -> mapAnnotations(annotations, alignment)
                )
        );
    }

}
