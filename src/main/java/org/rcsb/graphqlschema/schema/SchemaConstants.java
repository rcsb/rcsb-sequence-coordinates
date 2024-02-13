/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.schema;

import org.rcsb.mojave.CoreConstants;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class SchemaConstants {

    private final static String QUERY_ID = "queryId";
    private final static String FROM = "from";
    private final static String TO = "to";
    private final static String RANGE = "range";
    private final static String FILTER = "filter";
    private final static String FILTERS = "filters";
    private final static String REFERENCE = "reference";
    private final static String SOURCES = "sources";
    private final static String SOURCE = "source";
    private final static String GROUP_ID = "groupId";
    private final static String GROUP = "group";
    private final static String FIRST = "first";
    private final static String OFFSET = "offset";
    private final static String ALIGNMENT = "alignment";
    private final static String GROUP_ALIGNMENT = "group_alignment";
    private final static String ANNOTATIONS = "annotations";
    private final static String GROUP_ANNOTATIONS = "group_annotations";
    private final static String ALIGNMENT_SUBSCRIPTION = "alignment_subscription";
    private final static String GROUP_ALIGNMENT_SUBSCRIPTION = "group_alignment_subscription";
    private final static String ANNOTATIONS_SUBSCRIPTION = "annotations_subscription";
    private final static String GROUP_ANNOTATIONS_SUBSCRIPTION = "group_annotations_subscription";
    private final static String QUERY_SEQUENCE = "query_sequence";
    private final static String ALIGNMENT_LENGTH = "alignment_length";
    private final static String TARGET_ALIGNMENT = "target_alignment";
    private final static String TARGET_ID = "target_id";
    private final static String TARGET_SEQUENCE = "target_sequence";
    private final static String COVERAGE = "coverage";
    private final static String ALIGNED_REGIONS = "aligned_regions";
    private final static String ALIGNMENT_LOGO = "alignment_logo";
    /*
    * Why next attributes are not defined quoting strings ?
    * The main difference is that these attributes (getters and setters) are defined in mojave models classes:
    * org.rcsb.mojave.auto.Coverage and org.rcsb.mojave.auto.AlignedRegions.
    * For that reason, we use the same DW schema attribute names accessible from org.rcsb.mojave.CoreConstants
    * instead of defining them here.
    * */
    private final static String QUERY_COVERAGE = CoreConstants.QUERY_COVERAGE;
    private final static String TARGET_COVERAGE = CoreConstants.TARGET_COVERAGE;
    private final static String QUERY_LENGTH = CoreConstants.QUERY_LENGTH;
    private final static String TARGET_LENGTH = CoreConstants.TARGET_LENGTH;
    private final static String QUERY_BEGIN = CoreConstants.QUERY_BEGIN;
    private final static String QUERY_END = CoreConstants.QUERY_END;
    private final static String TARGET_BEGIN = CoreConstants.TARGET_BEGIN;
    private final static String TARGET_END = CoreConstants.TARGET_END;
    private final static String FEATURES = CoreConstants.FEATURES;
    private final static String FEATURE_POSITIONS = CoreConstants.FEATURE_POSITIONS;
    private final static String TYPE = CoreConstants.TYPE;
    private final static String BEG_SEQ_ID = CoreConstants.BEG_SEQ_ID;
    private final static String END_SEQ_ID = CoreConstants.END_SEQ_ID;
    private final static String BEG_ORI_ID = CoreConstants.BEG_ORI_ID;
    private final static String END_ORI_ID = CoreConstants.END_ORI_ID;
    private final static String VALUES = CoreConstants.VALUES;

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
        public final static String RANGE = SchemaConstants.RANGE;
        public final static String GROUP_FILTER = SchemaConstants.FILTER;
        public final static String ANNOTATION_FILTERS = SchemaConstants.FILTERS;
        public final static String REFERENCE = SchemaConstants.REFERENCE;
        public final static String SOURCES = SchemaConstants.SOURCES;
        public final static String GROUP_ID = SchemaConstants.GROUP_ID;
        public final static String GROUP = SchemaConstants.GROUP;
        public final static String FIRST = SchemaConstants.FIRST;
        public final static String OFFSET = SchemaConstants.OFFSET;
    }

    public static  class Field {
        public final static String QUERY_SEQUENCE = SchemaConstants.QUERY_SEQUENCE;
        public final static String ALIGNMENT_LENGTH = SchemaConstants.ALIGNMENT_LENGTH;
        public final static String TARGET_ALIGNMENT = SchemaConstants.TARGET_ALIGNMENT;
        public final static String TARGET_ID = SchemaConstants.TARGET_ID;
        public final static String TARGET_SEQUENCE = SchemaConstants.TARGET_SEQUENCE;
        public final static String COVERAGE = SchemaConstants.COVERAGE;
        public final static String ALIGNED_REGIONS = SchemaConstants.ALIGNED_REGIONS;
        public final static String ALIGNMENT_LOGO = SchemaConstants.ALIGNMENT_LOGO;
        public final static String QUERY_COVERAGE = SchemaConstants.QUERY_COVERAGE;
        public final static String TARGET_COVERAGE = SchemaConstants.TARGET_COVERAGE;
        public final static String QUERY_LENGTH = SchemaConstants.QUERY_LENGTH;
        public final static String TARGET_LENGTH = SchemaConstants.TARGET_LENGTH;
        public final static String QUERY_BEGIN = SchemaConstants.QUERY_BEGIN;
        public final static String QUERY_END = SchemaConstants.QUERY_END;
        public final static String TARGET_BEGIN = SchemaConstants.TARGET_BEGIN;
        public final static String TARGET_END = SchemaConstants.TARGET_END;
        public final static String FEATURES = SchemaConstants.FEATURES;
        public final static String FEATURE_POSITIONS = SchemaConstants.FEATURE_POSITIONS;
        public final static String TYPE = SchemaConstants.TYPE;
        public final static String BEG_SEQ_ID = SchemaConstants.BEG_SEQ_ID;
        public final static String END_SEQ_ID = SchemaConstants.END_SEQ_ID;
        public final static String BEG_ORI_ID = SchemaConstants.BEG_ORI_ID;
        public final static String END_ORI_ID = SchemaConstants.END_ORI_ID;
        public final static String VALUES = SchemaConstants.VALUES;
        public final static String SOURCE = SchemaConstants.SOURCE;

    }

}
