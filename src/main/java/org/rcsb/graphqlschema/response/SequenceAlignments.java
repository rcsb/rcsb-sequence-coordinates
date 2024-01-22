package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.schema.SchemaConstants;

import java.util.List;

public class SequenceAlignments {
    private String querySequence;
    private List<TargetAlignment> targetAlignments;
    @JsonProperty(SchemaConstants.Field.QUERY_SEQUENCE)
    public String getQuerySequence() {
        return this.querySequence;
    }
    @JsonProperty(SchemaConstants.Field.QUERY_SEQUENCE)
    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_ALIGNMENTS)
    public List<TargetAlignment> getTargetAlignments(){
        return this.targetAlignments;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_ALIGNMENTS)
    public void setTargetAlignments(List<TargetAlignment> targetAlignments){
        this.targetAlignments = targetAlignments;
    }
}
