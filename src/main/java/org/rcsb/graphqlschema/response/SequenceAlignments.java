package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.schema.SchemaConstants;

import java.util.List;

public class SequenceAlignments {
    private String querySequence;
    private List<TargetAlignment> targetAlignment;
    @JsonProperty(SchemaConstants.Field.QUERY_SEQUENCE)
    public String getQuerySequence() {
        return this.querySequence;
    }
    @JsonProperty(SchemaConstants.Field.QUERY_SEQUENCE)
    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_ALIGNMENT)
    public List<TargetAlignment> getTargetAlignment(){
        return this.targetAlignment;
    }
    @JsonProperty(SchemaConstants.Field.TARGET_ALIGNMENT)
    public void setTargetAlignment(List<TargetAlignment> targetAlignment){
        this.targetAlignment = targetAlignment;
    }
}
