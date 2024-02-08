/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/8/24, Thursday
 **/
public class SequenceAnnotations extends org.rcsb.mojave.auto.SequenceAnnotations {

    private AnnotationReference source;

    @JsonProperty(SchemaConstants.Field.SOURCE)
    public AnnotationReference getSource(){
        return source;
    }
    @JsonProperty(SchemaConstants.Field.SOURCE)
    public void setSource(AnnotationReference source) {
        this.source = source;
    }

}
