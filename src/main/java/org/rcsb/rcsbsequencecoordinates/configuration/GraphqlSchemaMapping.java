package org.rcsb.rcsbsequencecoordinates.configuration;

import java.util.List;

import org.rcsb.graphqlschema.schema.SchemaConstants;

public class GraphqlSchemaMapping {

    public static final String QUERY_SEQUENCE = SchemaConstants.Field.QUERY_SEQUENCE;
    public static final String ALIGNMENT_LOGO = SchemaConstants.Field.ALIGNMENT_LOGO;
    public static final String TARGET_ALIGNMENTS = SchemaConstants.Field.TARGET_ALIGNMENTS;
    public static final String TARGET_SEQUENCE = SchemaConstants.Field.TARGET_SEQUENCE;
    public static List<String> getFields() {
        return List.of(QUERY_SEQUENCE, ALIGNMENT_LOGO, TARGET_ALIGNMENTS,TARGET_SEQUENCE);
    }

}
