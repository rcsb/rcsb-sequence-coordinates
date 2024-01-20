package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.schema.SchemaFieldConstants;

import java.util.List;

public class SequenceAlignments {
    private String querySequence;
    private List<TargetAlignment> targetAlignments;
    @JsonProperty(SchemaFieldConstants.QUERY_SEQUENCE)
    public String getQuerySequence() {
        return this.querySequence;
    }
    @JsonProperty(SchemaFieldConstants.QUERY_SEQUENCE)
    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
    @JsonProperty(SchemaFieldConstants.TARGET_ALIGNMENTS)
    public List<TargetAlignment> getTargetAlignments(){
        return this.targetAlignments;
    }
    @JsonProperty(SchemaFieldConstants.TARGET_ALIGNMENTS)
    public void setTargetAlignments(List<TargetAlignment> targetAlignments){
        this.targetAlignments = targetAlignments;
    }
}
