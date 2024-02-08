/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.params;

import io.leangen.graphql.annotations.GraphQLNonNull;
import org.rcsb.graphqlschema.reference.AnnotationReference;

import java.io.Serializable;
import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/8/24, Thursday
 **/

public class AnnotationFilter implements Serializable {

    @GraphQLNonNull
    private FieldName field;
    @GraphQLNonNull
    private OperationType operation;
    private AnnotationReference source;
    @GraphQLNonNull
    private List<@GraphQLNonNull String> values;

    @GraphQLNonNull
    public FieldName getField() {
        return field;
    }
    public void setField(FieldName field) {
        this.field = field;
    }

    @GraphQLNonNull
    public OperationType getOperation(){
        return operation;
    }
    public void setOperation(OperationType operation){
        this.operation = operation;
    }

    public AnnotationReference getSource(){
        return source;
    }
    public void setSource(AnnotationReference source){
        this.source = source;
    }

    @GraphQLNonNull
    public List<@GraphQLNonNull String> getValues() {
        return values;
    }
    public void setValues(List<String> values) {
        this.values = values;
    }

    public enum OperationType {
        EQUALS("equals"),
        CONTAINS("contains");
        private final String value;
        OperationType(String value) {
            this.value = value;
        }
        public String toString() {
            return this.value;
        }
    }

    public enum FieldName {
        TARGET_ID("target_id"),
        TYPE("type");
        private final String value;
        FieldName(String value) {
            this.value = value;
        }
        public String toString() {
            return this.value;
        }
    }

}
