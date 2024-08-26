/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.schema.SchemaConstants;

import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class SequenceAlignments {

    public static final String CLASS_NAME = "SequenceAlignments";
    private String querySequence;
    private List<TargetAlignments> targetAlignments;
    private Integer alignmentLength;
    private List<List<AlignmentLogo>> alignmentLogo;

    @JsonProperty(SchemaConstants.Field.QUERY_SEQUENCE)
    public String getQuerySequence() {
        return this.querySequence;
    }
    @JsonProperty(SchemaConstants.Field.QUERY_SEQUENCE)
    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_ALIGNMENTS)
    public List<TargetAlignments> getTargetAlignment(){
        return this.targetAlignments;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_ALIGNMENTS)
    public void setTargetAlignment(List<TargetAlignments> targetAlignments){
        this.targetAlignments = targetAlignments;
    }

    @JsonProperty(SchemaConstants.Field.ALIGNMENT_LENGTH)
    public Integer getAlignmentLength() {
        return alignmentLength;
    }
    @JsonProperty(SchemaConstants.Field.ALIGNMENT_LENGTH)
    public void setAlignmentLength(Integer alignmentLength) {
        this.alignmentLength = alignmentLength;
    }

    @JsonProperty(SchemaConstants.Field.ALIGNMENT_LOGO)
    public List<List<AlignmentLogo>> getAlignmentLogo() {
        return alignmentLogo;
    }
    @JsonProperty(SchemaConstants.Field.ALIGNMENT_LOGO)
    public void setAlignmentLogo(List<List<AlignmentLogo>> alignmentLogo) {
        this.alignmentLogo = alignmentLogo;
    }

}
