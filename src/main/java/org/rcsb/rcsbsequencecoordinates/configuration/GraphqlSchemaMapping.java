/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.configuration;

import java.util.List;

import org.rcsb.graphqlschema.schema.SchemaConstants;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class GraphqlSchemaMapping {

    public static final String QUERY_SEQUENCE = SchemaConstants.Field.QUERY_SEQUENCE;
    public static final String ALIGNMENT_LOGO = SchemaConstants.Field.ALIGNMENT_LOGO;
    public static final String TARGET_ALIGNMENTS = SchemaConstants.Field.TARGET_ALIGNMENTS;
    public static final String TARGET_SEQUENCE = SchemaConstants.Field.TARGET_SEQUENCE;
    public static final String ALIGNMENT_LENGTH = SchemaConstants.Field.ALIGNMENT_LENGTH;
    public static List<String> getFields() {
        return List.of(
                QUERY_SEQUENCE,
                ALIGNMENT_LOGO,
                TARGET_ALIGNMENTS,
                TARGET_SEQUENCE,
                ALIGNMENT_LENGTH
        );
    }

}
