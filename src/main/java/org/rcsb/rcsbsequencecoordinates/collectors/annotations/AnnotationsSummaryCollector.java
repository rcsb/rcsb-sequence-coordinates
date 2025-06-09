/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.annotations;

import org.bson.Document;
import org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentLengthCollector;
import org.rcsb.rcsbsequencecoordinates.collectors.utils.AnnotationSourceMap;
import org.rcsb.graphqlschema.params.AnnotationFilter;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.auto.FeaturesAdditionalPropertiesPropertyName;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.rcsb.rcsbsequencecoordinates.collectors.annotations.AnnotationSummaryHelper.mergeFeaturePositions;

/**
 * @author : joan
 */
@Service
public class AnnotationsSummaryCollector {

    private final AnnotationsCollector annotationsCollector;

    public AnnotationsSummaryCollector() {
        this.annotationsCollector = new AnnotationsCollector();
    }

    public Flux<Document> getAnnotations(
            String groupId,
            GroupReference groupReference,
            List<AnnotationReference> annotationReferences,
            List<AnnotationFilter> annotationFilters
    ){
        return annotationsCollector.getAnnotations(groupId, groupReference, annotationReferences, annotationFilters)
                .reduce(new AnnotationSourceMap(), this::addAnnotation)
                .flatMapMany(annotationSourceMap -> annotationsSummary(groupId, groupReference, annotationSourceMap));
    }

    private Flux<Document> annotationsSummary(
            String groupId,
            GroupReference groupReference,
            AnnotationSourceMap annotationSourceMap
    ){
        return AlignmentLengthCollector.request(groupId, groupReference)
                .flatMapMany(alignmentLength-> annotationsSummary(alignmentLength, annotationSourceMap));
    }

    private Flux<Document> annotationsSummary(
            int alignmentLength,
            AnnotationSourceMap annotationSourceMap
    ){
        return Flux.fromStream(
                annotationSourceMap.entrySet()
                        .stream()
                        .map(sourceEntry-> new Document(Map.of(
                                SchemaConstants.Field.SOURCE, sourceEntry.getKey(),
                                SchemaConstants.Field.TARGET_ID, "multiple-targets",
                                SchemaConstants.Field.FEATURES, featureSummary(alignmentLength, sourceEntry.getValue())
                        )))
        );
    }

    private List<Document> featureSummary(
            int alignmentLength,
            AnnotationSourceMap.AnnotationTypeMap annotationTypeMap
    ) {
        return annotationTypeMap.entrySet().stream().flatMap(
                typeEntry -> featureSummary(alignmentLength, typeEntry.getKey(), typeEntry.getValue()).stream()
        ).toList();
    }


    private List<Document> featureSummary(
            int alignmentLength,
            String type,
            AnnotationSourceMap.AnnotationTypeMap.AnnotationNameMap annotationNameMap
    ) {
        return annotationNameMap.entrySet().stream().map(
                nameEntry -> featureSummary(
                        alignmentLength,
                        type,
                        nameEntry.getKey(),
                        annotationNameMap.getTargets( nameEntry.getKey() ),
                        nameEntry.getValue()
                )
        ).toList();
    }

    private Document featureSummary(
            int alignmentLength,
            String type,
            String name,
            Set<String> targetIds,
            List<Document> features
    ){
        return new Document(Map.of(
                SchemaConstants.Field.TYPE, type,
                SchemaConstants.Field.NAME, name != null ? name : type,
                SchemaConstants.Field.VALUE, targetIds.size(),
                SchemaConstants.Field.ADDITIONAL_PROPERTIES, targetList(targetIds),
                SchemaConstants.Field.FEATURE_POSITIONS, mergeFeaturePositions(alignmentLength, features)
        ));
    }

    private List<Document> targetList(Set<String> targetIds){
        return List.of(new Document(Map.of(
                SchemaConstants.Field.PROPERTY_NAME, FeaturesAdditionalPropertiesPropertyName.TARGET_ID,
                SchemaConstants.Field.PROPERTY_VALUE, targetIds
        )));
    }

    private AnnotationSourceMap addAnnotation(AnnotationSourceMap annotationSourceMap, Document annotation){
        annotation.getList(SchemaConstants.Field.FEATURES, Document.class).forEach(
                feature -> addFeature(annotationSourceMap, annotation, feature)
        );
        return annotationSourceMap;
    }

    private void addFeature(AnnotationSourceMap annotationSourceMap, Document annotation, Document feature){
        String source = annotation.get(SchemaConstants.Field.SOURCE, AnnotationReference.class).toString();
        String type = feature.getString(SchemaConstants.Field.TYPE);
        String name = feature.getString(SchemaConstants.Field.NAME);
        String targetId = annotation.getString(SchemaConstants.Field.TARGET_ID);
        annotationSourceMap.get(source, type, name, targetId).add(feature);
    }

}
