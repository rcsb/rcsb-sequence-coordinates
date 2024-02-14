/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rcsb.graphqlschema.schema.SchemaConstants;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/13/24, Tuesday
 **/
public class AlignmentLogo {

    private String symbol;
    private Integer value;

    @JsonProperty(SchemaConstants.Field.VALUE)
    public Integer getValue() {
        return value;
    }
    @JsonProperty(SchemaConstants.Field.VALUE)
    public void setValue(Integer value) {
        this.value = value;
    }

    @JsonProperty(SchemaConstants.Field.SYMBOL)
    public String getSymbol() {
        return symbol;
    }
    @JsonProperty(SchemaConstants.Field.SYMBOL)
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
