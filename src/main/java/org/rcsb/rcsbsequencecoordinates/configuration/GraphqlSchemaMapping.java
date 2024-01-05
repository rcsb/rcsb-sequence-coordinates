package org.rcsb.rcsbsequencecoordinates.configuration;

import org.rcsb.rcsbsequencecoordinates.auto.AnnConstants;

import java.util.Arrays;
import java.util.List;

public class GraphqlSchemaMapping {

    public static final String QUERY_SEQUENCE = AnnConstants.QUERY_SEQUENCE;
    public static final String ALIGNMENT_LOGO = AnnConstants.ALIGNMENT_LOGO;

    public static List<String> getFields() {
        return Arrays.asList(QUERY_SEQUENCE, ALIGNMENT_LOGO);
    }

}
