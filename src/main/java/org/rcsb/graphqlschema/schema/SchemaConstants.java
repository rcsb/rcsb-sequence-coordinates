package org.rcsb.graphqlschema.schema;

public class SchemaConstants {

    private final static String QUERY_ID = "queryId";
    private final static String FROM = "from";
    private final static String TO = "to";
    private final static String REFERENCE = "reference";
    private final static String SOURCES = "sources";
    private final static String GROUP_ID = "groupId";
    private final static String GROUP = "group";
    private final static String ALIGNMENT = "alignment";
    private final static String GROUP_ALIGNMENT = "group_alignment";
    private final static String ANNOTATIONS = "annotations";
    private final static String GROUP_ANNOTATIONS = "group_annotations";
    private final static String ALIGNMENT_SUBSCRIPTION = "alignment_subscription";
    private final static String GROUP_ALIGNMENT_SUBSCRIPTION = "group_alignment_subscription";
    private final static String ANNOTATIONS_SUBSCRIPTION = "annotations_subscription";
    private final static String GROUP_ANNOTATIONS_SUBSCRIPTION = "group_annotations_subscription";
    private final static String QUERY_SEQUENCE = "query_sequence";
    private final static String TARGET_ALIGNMENT = "target_alignment";
    private final static String TARGET_ID = "target_id";
    private final static String TARGET_SEQUENCE = "target_sequence";
    private final static String COVERAGE = "coverage";
    private final static String ALIGNED_REGIONS = "aligned_regions";
    private final static String ALIGNMENT_LOGO = "alignment_logo";

    public static class Query {
        public final static String ALIGNMENT = SchemaConstants.ALIGNMENT;
        public final static String GROUP_ALIGNMENT = SchemaConstants.GROUP_ALIGNMENT;
        public final static String ANNOTATIONS = SchemaConstants.ANNOTATIONS;
        public final static String GROUP_ANNOTATIONS = SchemaConstants.GROUP_ANNOTATIONS;
    }

    public static class Subscription {
        public final static String ALIGNMENT_SUBSCRIPTION = SchemaConstants.ALIGNMENT_SUBSCRIPTION;
        public final static String GROUP_ALIGNMENT_SUBSCRIPTION = SchemaConstants.GROUP_ALIGNMENT_SUBSCRIPTION;
        public final static String ANNOTATIONS_SUBSCRIPTION = SchemaConstants.ANNOTATIONS_SUBSCRIPTION;
        public final static String GROUP_ANNOTATIONS_SUBSCRIPTION = SchemaConstants.GROUP_ANNOTATIONS_SUBSCRIPTION;
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
        public final static String TARGET_ALIGNMENT = SchemaConstants.TARGET_ALIGNMENT;
        public final static String TARGET_ID = SchemaConstants.TARGET_ID;
        public final static String TARGET_SEQUENCE = SchemaConstants.TARGET_SEQUENCE;
        public final static String COVERAGE = SchemaConstants.COVERAGE;
        public final static String ALIGNED_REGIONS = SchemaConstants.ALIGNED_REGIONS;
        public final static String ALIGNMENT_LOGO = SchemaConstants.ALIGNMENT_LOGO;
    }

}
