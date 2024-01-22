package org.rcsb.graphqlschema.schema;

public class SchemaConstants {

    private final static String QUERY_ID = "queryId";
    private final static String FROM = "from";
    private final static String TO = "to";
    private final static String REFERENCE = "reference";
    private final static String SOURCES = "sources";
    private final static String GROUP_ID = "groupId";
    private final static String GROUP = "group";
    private final static String ALIGNMENTS = "alignments";
    private final static String GROUP_ALIGNMENTS = "group_alignments";
    private final static String ANNOTATIONS = "annotations";
    private final static String GROUP_ANNOTATIONS = "group_annotations";
    private final static String QUERY_SEQUENCE = "query_sequence";
    private final static String TARGET_ALIGNMENTS = "target_alignments";
    private final static String TARGET_ID = "target_id";
    private final static String TARGET_SEQUENCE = "target_sequence";
    private final static String SCORES = "scores";
    private final static String ALIGNED_REGIONS = "aligned_regions";
    private final static String ALIGNMENT_LOGO = "alignment_logo";

    public static class Query {
        public final static String ALIGNMENTS = SchemaConstants.ALIGNMENTS;
        public final static String GROUP_ALIGNMENTS = SchemaConstants.GROUP_ALIGNMENTS;
        public final static String ANNOTATIONS = SchemaConstants.ANNOTATIONS;
        public final static String GROUP_ANNOTATIONS = SchemaConstants.GROUP_ANNOTATIONS;
    }

    public static class Param {
        public final static String QUERY_ID = SchemaConstants.QUERY_ID;
        public final static String FROM = SchemaConstants.FROM;
        public final static String TO = SchemaConstants.TO;
        public final static String REFERENCE = SchemaConstants.REFERENCE;
        public final static String SOURCES = SchemaConstants.SOURCES;
        public final static String GROUP_ID = SchemaConstants.GROUP_ID;
        public final static String GROUP = SchemaConstants.GROUP;
    }

    public static  class Field {
        public final static String QUERY_SEQUENCE = SchemaConstants.QUERY_SEQUENCE;
        public final static String TARGET_ALIGNMENTS = SchemaConstants.TARGET_ALIGNMENTS;
        public final static String TARGET_ID = SchemaConstants.TARGET_ID;
        public final static String TARGET_SEQUENCE = SchemaConstants.TARGET_SEQUENCE;
        public final static String SCORES = SchemaConstants.SCORES;
        public final static String ALIGNED_REGIONS = SchemaConstants.ALIGNED_REGIONS;
        public final static String ALIGNMENT_LOGO = SchemaConstants.ALIGNMENT_LOGO;
    }


}
