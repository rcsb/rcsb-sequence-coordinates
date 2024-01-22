package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.auto.Scores;
import org.rcsb.mojave.auto.AlignedRegions;

import java.util.List;

public class TargetAlignment {
    private String targetId;
    private String targetSequence;
    private Scores scores;
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
    @JsonProperty(SchemaConstants.Field.SCORES)
    public Scores getScores(){
        return this.scores;
    }
    @JsonProperty(SchemaConstants.Field.SCORES)
    public void setScores(Scores scores){
        this.scores = scores;
    }
    @JsonProperty(SchemaConstants.Field.ALIGNED_REGIONS)
    public List<AlignedRegions> getAlignedRegions(){
        return this.alignedRegions;
    }
    @JsonProperty(SchemaConstants.Field.ALIGNED_REGIONS)
    public void setAlignedRegions(List<AlignedRegions> alignedRegions){
        this.alignedRegions = alignedRegions;
    }
}
