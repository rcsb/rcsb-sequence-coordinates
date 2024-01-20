package org.rcsb.rcsbsequencecoordinates.configuration;

import java.util.List;

import org.rcsb.graphqlschema.schema.SchemaFieldConstants;

public class GraphqlSchemaMapping {

    public static final String QUERY_SEQUENCE = SchemaFieldConstants.QUERY_SEQUENCE;
    public static final String ALIGNMENT_LOGO = SchemaFieldConstants.ALIGNMENT_LOGO;
    public static final String TARGET_ALIGNMENTS = SchemaFieldConstants.TARGET_ALIGNMENTS;
    public static final String TARGET_SEQUENCE = SchemaFieldConstants.TARGET_SEQUENCE;
    public static List<String> getFields() {
        return List.of(QUERY_SEQUENCE, ALIGNMENT_LOGO, TARGET_ALIGNMENTS,TARGET_SEQUENCE);
    }

}
