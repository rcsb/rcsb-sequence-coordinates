/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.annotations;

import org.bson.Document;
import org.rcsb.rcsbsequencecoordinates.collectors.alignments.SequenceAlignmentsCollector;
import org.rcsb.rcsbsequencecoordinates.collectors.utils.AnnotationFilterOperator;
import org.rcsb.rcsbsequencecoordinates.collectors.utils.AnnotationRangeIntersection;
import org.rcsb.rcsbsequencecoordinates.collectors.utils.RangeIntersectionOperator;
import org.rcsb.graphqlschema.params.AnnotationFilter;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.rcsbsequencecoordinates.utils.ReactiveMongoResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsMongoHelper.getTargetIndex;
import static org.rcsb.rcsbsequencecoordinates.collectors.annotations.AnnotationsHelper.*;

/**
 * @author : joan
 */
@Service
public class AnnotationsCollector {

    private final ReactiveMongoResource mongoResource;
    private final SequenceAlignmentsCollector sequenceAlignmentsCollector;

    @Autowired
    public AnnotationsCollector(ReactiveMongoResource mongoResource) {
        this.mongoResource = mongoResource;
        this.sequenceAlignmentsCollector = new SequenceAlignmentsCollector(mongoResource);
    }

    public Flux<Document> getAnnotations(
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

    public Flux<Document> getAnnotations(
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

    private Flux<Document> getAnnotations(
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

    private Flux<Document> getAnnotations(
            String groupId,
            GroupReference groupReference,
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters
    ) {
        return AnnotationFilterOperator.build(annotationFilters, mongoResource)
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

    private Flux<Document> getAnnotations(
            String groupId,
            GroupReference groupReference,
            List<String> groupFilter,
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters
    ) {
        return sequenceAlignmentsCollector
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

    private Flux<Document> getAnnotations(
            String queryId,
            SequenceReference sequenceReference,
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters
    ) {
        return sequenceAlignmentsCollector
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
    private Flux<Document> getAnnotations(
            AnnotationReference annotationReference,
            List<AnnotationFilter> annotationFilters,
            Document alignment
    ) {
        AnnotationFilterOperator filter = AnnotationFilterOperator.build(annotationFilters, mongoResource);
        return Flux.from(mongoResource.getDatabase().getCollection(getCollection(annotationReference)).aggregate(
                       getAggregation(alignment.getString(getTargetIndex()), annotationReference)
               ))
               .map(annotations -> addSource(annotationReference, annotations))
               .filter(filter::targetCheck)
               .map(filter::applyFilterToFeatures)
               .filter(AnnotationsHelper::hasFeatures)
               .map(annotations -> mapAnnotations(annotations, alignment));
    }

    private Flux<Document> switchAlignmentEntityIdToReference(Document alignment, SequenceReference reference){
        return sequenceAlignmentsCollector
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
