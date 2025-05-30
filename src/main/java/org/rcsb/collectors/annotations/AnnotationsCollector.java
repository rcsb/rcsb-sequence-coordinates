/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.annotations;

import org.bson.Document;
import org.rcsb.collectors.alignments.SequenceAlignmentsCollector;
import org.rcsb.collectors.utils.AnnotationFilterOperator;
import org.rcsb.collectors.utils.AnnotationRangeIntersection;
import org.rcsb.collectors.utils.RangeIntersectionOperator;
import org.rcsb.graphqlschema.params.AnnotationFilter;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.utils.MongoStream;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.rcsb.collectors.alignments.AlignmentsMongoHelper.getTargetIndex;
import static org.rcsb.collectors.annotations.AnnotationsHelper.*;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class AnnotationsCollector {

    public static Flux<Document> getAnnotations(
            String queryId,
            SequenceReference sequenceReference,
            List<AnnotationReference> annotationReferences,
            List<AnnotationFilter> annotationFilters,
            List<Integer> range
    ) {
        RangeIntersectionOperator annotationRangeIntersection = new RangeIntersectionOperator(range, new AnnotationRangeIntersection());
        return getAnnotations(queryId, sequenceReference, annotationReferences, annotationFilters)
                .filter(annotationRangeIntersection::isConnected)
                .map(annotationRangeIntersection::applyRange);
    }

    public static Flux<Document> getAnnotations(
            String groupId,
            GroupReference groupReference,
            List<AnnotationReference> annotationReferences,
            List<AnnotationFilter> annotationFilters
    ){
        if(groupReference.equals(GroupReference.MATCHING_UNIPROT_ACCESSION))
            return getAnnotations(groupId, SequenceReference.UNIPROT, annotationReferences, annotationFilters);
        return Flux.fromIterable(annotationReferences)
                .flatMap(
                        annotationReference -> getAnnotations(groupId, groupReference, annotationReference, annotationFilters)
                );
    }

    private static Flux<Document> getAnnotations(
            String queryId,
            SequenceReference sequenceReference,
            List<AnnotationReference> annotationReferences,
            List<AnnotationFilter> annotationFilters
    ) {
        return Flux.fromIterable(annotationReferences)
                .flatMap(
                        annotationReference -> getAnnotations(queryId, sequenceReference, annotationReference, annotationFilters)
                );
    }

    private static Flux<Document> getAnnotations(
            String groupId,
            GroupReference groupReference,
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters
    ) {
        return AnnotationFilterOperator.build(annotationFilters)
                .applyToAlignments(SequenceReference.PDB_ENTITY, annotationReference)
                .collectList()
                .flatMapMany(
                        groupFilter-> getAnnotations(
                                groupId,
                                groupReference,
                                groupFilter,
                                annotationReference,
                                annotationFilters
                        )
                );
    }

    private static Flux<Document> getAnnotations(
            String groupId,
            GroupReference groupReference,
            List<String> groupFilter,
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters
    ) {
        return SequenceAlignmentsCollector
                .request(
                        groupId,
                        groupReference
                )
                .filter(
                    groupFilter
                )
                .get()
                .flatMap(
                    groupAlignment -> switchAlignmentEntityIdToReference(groupAlignment, annotationReference.toSequenceReference())
                )
                .flatMap(
                    alignment -> getAnnotations(annotationReference, annotationFilters, alignment)
                );
    }

    private static Flux<Document> getAnnotations(
            String queryId,
            SequenceReference sequenceReference,
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters
    ) {
        return SequenceAlignmentsCollector
                .request(
                    queryId,
                    sequenceReference,
                    annotationReference.toSequenceReference()
                )
                .get()
                .flatMap(
                    alignment -> getAnnotations(annotationReference, annotationFilters, alignment)
                );
    }
    private static Flux<Document> getAnnotations(
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters,
            Document alignment
    ) {
        AnnotationFilterOperator filter = AnnotationFilterOperator.build(annotationFilters);
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getCollection(annotationReference)).aggregate(
                       getAggregation(alignment.getString(getTargetIndex()), annotationReference)
               ))
               .map(annotations -> addSource(annotationReference, annotations))
               .filter(filter::targetCheck)
               .map(filter::applyFilterToFeatures)
               .filter(AnnotationsHelper::hasFeatures)
               .map(annotations -> mapAnnotations(annotations, alignment));
    }

    private static Flux<Document> switchAlignmentEntityIdToReference(Document alignment, SequenceReference reference){
        return SequenceAlignmentsCollector
            .request(
                    alignment.getString(getTargetIndex()),
                    SequenceReference.PDB_ENTITY,
                    reference
            )
            .get()
            .map(entityAlignment->{
                alignment.put(
                        SchemaConstants.Field.TARGET_ID,
                        entityAlignment.getString(getTargetIndex())
                );
                return alignment;
            });
    }

}
