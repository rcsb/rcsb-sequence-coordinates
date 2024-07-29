/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.auto.Coverage;
import org.rcsb.mojave.auto.AlignedRegions;

import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class TargetAlignment {
    public static final String CLASS_NAME = "TargetAlignment";
    private String targetId;
    private String targetSequence;
    private Coverage coverage;
    private Integer orientation;
    private List<AlignedRegions> alignedRegions;
    @JsonProperty(SchemaConstants.Field.TARGET_ID)
    public String getTargetId(){
        return this.targetId;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_ID)
    public void setTargetId(String targetId){
        this.targetId = targetId;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_SEQUENCE)
    public String getTargetSequence() {
        return this.targetSequence;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_SEQUENCE)
    public void setTargetSequence(String targetSequence) {
        this.targetSequence = targetSequence;
    }
    @JsonProperty(SchemaConstants.Field.COVERAGE)
    public Coverage getCoverage(){
        return this.coverage;
    }
    @JsonProperty(SchemaConstants.Field.COVERAGE)
    public void setCoverage(Coverage coverage){
        this.coverage = coverage;
    }
    @JsonProperty(SchemaConstants.Field.ALIGNED_REGIONS)
    public List<AlignedRegions> getAlignedRegions(){
        return this.alignedRegions;
    }
    @JsonProperty(SchemaConstants.Field.ALIGNED_REGIONS)
    public void setAlignedRegions(List<AlignedRegions> alignedRegions){
        this.alignedRegions = alignedRegions;
    }
    @JsonProperty(SchemaConstants.Field.ORIENTATION)
    public Integer getOrientation() {
        return orientation;
    }
    @JsonProperty(SchemaConstants.Field.ORIENTATION)
    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }
}
