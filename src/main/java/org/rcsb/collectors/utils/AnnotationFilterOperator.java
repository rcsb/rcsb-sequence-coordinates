/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.utils;

import org.bson.Document;
import org.rcsb.graphqlschema.params.AnnotationFilter;
import org.rcsb.graphqlschema.params.AnnotationFilter.FieldName;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/8/24, Thursday
 **/
public class AnnotationFilterOperator {

    private final List<AnnotationFilter> annotationFilter;

    public AnnotationFilterOperator(List<AnnotationFilter> annotationFilter){
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
                        f -> filterCheck(FieldName.TARGET_ID, f)
                )
                .filter(
                        f -> filterCheck(annotations.get(SchemaConstants.Field.SOURCE, AnnotationReference.class), f)
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

    private List<Document> filterFeatures(Document annotations){
        return annotations.getList(SchemaConstants.Field.FEATURES,Document.class).stream()
                .filter(
                        feature -> filterFeature(annotations.get(SchemaConstants.Field.SOURCE, AnnotationReference.class), feature)
                )
                .collect(Collectors.toList());
    }

    private boolean filterFeature(AnnotationReference annotationReference, Document feature){
        if(this.annotationFilter == null)
            return true;
        return annotationFilter.stream()
                .filter(
                        f -> filterCheck(FieldName.TYPE, f)
                )
                .filter(
                        f -> filterCheck(annotationReference, f)
                )
                .allMatch(
                        f -> valueCheck(feature.getString(SchemaConstants.Field.TYPE), f)
                );
    }

    private boolean filterCheck(FieldName fieldName, AnnotationFilter filter){
        return filter.getField().equals(fieldName);
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

}
