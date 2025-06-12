/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.utils;

import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.Document;
import org.rcsb.rcsbsequencecoordinates.collectors.alignments.SequenceAlignmentsCollector;
import org.rcsb.graphqlschema.params.AnnotationFilter;
import org.rcsb.graphqlschema.params.AnnotationFilter.FieldName;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import reactor.core.publisher.Flux;

import java.util.*;

import static org.rcsb.rcsbsequencecoordinates.collectors.annotations.AnnotationsHelper.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.map.MapHelper.instanceFromInstanceMap;

/**
 * @author : joan
 */
public class AnnotationFilterOperator {

    private final List<AnnotationFilter> annotationFilter;
    private final SequenceAlignmentsCollector sequenceAlignmentsCollector;
    private final MongoClient mongoClient;
    private final MongoProperties mongoProperties;

    public static AnnotationFilterOperator build(List<AnnotationFilter> annotationFilter, MongoClient mongoClient, MongoProperties mongoProperties) {
        return new AnnotationFilterOperator(annotationFilter, mongoClient, mongoProperties);
    }

    private AnnotationFilterOperator(List<AnnotationFilter> annotationFilter, MongoClient mongoClient, MongoProperties mongoProperties) {
        this.mongoClient = mongoClient;
        this.mongoProperties = mongoProperties;
        this.sequenceAlignmentsCollector = new SequenceAlignmentsCollector(mongoClient, mongoProperties);
        if(annotationFilter == null || annotationFilter.isEmpty())
            this.annotationFilter = null;
        else
            this.annotationFilter = annotationFilter;
    }

    public boolean targetCheck(Document annotations){
        if(this.annotationFilter == null)
            return true;
        return annotationFilter.stream()
                .filter(
                        f -> filterCheck(FieldName.TARGET_ID, f) && filterCheck(annotations.get(SchemaConstants.Field.SOURCE, AnnotationReference.class), f)
                )
                .allMatch(
                        f -> valueCheck(annotations.getString(SchemaConstants.Field.TARGET_ID), f)
                );
    }

    public Document applyFilterToFeatures(Document annotations){
        if(annotationFilter == null)
            return annotations;
        annotations.put(
                SchemaConstants.Field.FEATURES,
                filterFeatures(annotations)
        );
        return annotations;
    }

    public Flux<String> applyToAlignments(SequenceReference sequenceReference, AnnotationReference annotationReference) {
        if(annotationFilter == null)
            return Flux.just();
        return Flux.fromStream(annotationFilter.stream())
                .filter(f -> filterApplyToAlignmentsCheck(annotationReference, f))
                .flatMap(f->mapIds(annotationReference, f.getValues()))
                .collectList()
                .filter(list-> !list.isEmpty())
                .flatMapMany(
                        ids -> sequenceAlignmentsCollector.mapIds(annotationReference.toSequenceReference(), sequenceReference, ids)
                );
    }

    private Flux<String> mapIds(AnnotationReference annotationReference, List<String>ids){
        if( !annotationReference.equals(AnnotationReference.PDB_INTERFACE) )
            return Flux.fromIterable(ids);
        return Flux.from(mongoClient.getDatabase(mongoProperties.getDatabase()).getCollection(getCollection(annotationReference)).aggregate(
                getAggregation(ids)
        )).map(
                interfaceMap -> instanceFromInstanceMap(interfaceMap.get(getTargetIdentifiersAttribute(), Document.class))
        );
    }

    private List<Document> filterFeatures(Document annotations){
        return annotations.getList(SchemaConstants.Field.FEATURES,Document.class).stream()
                .filter(
                        feature -> filterFeature(annotations.get(SchemaConstants.Field.SOURCE, AnnotationReference.class), feature)
                )
                .toList();
    }

    private boolean filterFeature(AnnotationReference annotationReference, Document feature){
        if(this.annotationFilter == null)
            return true;
        return annotationFilter.stream()
                .filter(
                        f -> filterCheck(FieldName.TYPE, f) && filterCheck(annotationReference, f)
                )
                .allMatch(
                        f -> valueCheck(feature.getString(SchemaConstants.Field.TYPE).toUpperCase(), f)
                );
    }

    private boolean filterCheck(FieldName fieldName, AnnotationFilter filter){
        return filter.getField().equals(fieldName);
    }

    private boolean filterCheck(AnnotationFilter.OperationType operationType, AnnotationFilter filter){
        return filter.getOperation().equals(operationType);
    }

    private boolean filterCheck(AnnotationReference annotationReference, AnnotationFilter filter){
        if(filter.getSource() == null)
            return true;
        return filter.getSource().equals(annotationReference);
    }

    private boolean valueCheck(String value, AnnotationFilter annotationFilter){
        if(annotationFilter.getOperation().equals(AnnotationFilter.OperationType.EQUALS))
            return annotationFilter.getValues().stream().anyMatch(value::equals);
        if(annotationFilter.getOperation().equals(AnnotationFilter.OperationType.CONTAINS))
            return annotationFilter.getValues().stream().anyMatch(value::contains);
        return false;
    }

    private boolean filterApplyToAlignmentsCheck(AnnotationReference annotationReference, AnnotationFilter f) {
        return  (f.getSource() == null || filterCheck(annotationReference, f)) &&
                filterCheck(AnnotationFilter.OperationType.EQUALS, f) &&
                filterCheck(FieldName.TARGET_ID, f);
    }

}
