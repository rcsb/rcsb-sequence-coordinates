/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.schema;

import org.rcsb.mojave.SequenceCoordinatesConstants;

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
    private final static String RANGE_ID = "range_id";
    private final static String GROUP_ID = "groupId";
    private final static String GROUP = "group";
    private final static String FIRST = "first";
    private final static String OFFSET = "offset";
    private final static String ALIGNMENTS = "alignments";
    private final static String GROUP_ALIGNMENTS = "group_alignments";
    private final static String ANNOTATIONS = "annotations";
    private final static String GROUP_ANNOTATIONS = "group_annotations";
    private final static String GROUP_ANNOTATIONS_SUMMARY = "group_annotations_summary";
    private final static String ALIGNMENTS_SUBSCRIPTION = "alignments_subscription";
    private final static String GROUP_ALIGNMENTS_SUBSCRIPTION = "group_alignments_subscription";
    private final static String ANNOTATIONS_SUBSCRIPTION = "annotations_subscription";
    private final static String GROUP_ANNOTATIONS_SUBSCRIPTION = "group_annotations_subscription";
    private final static String QUERY_SEQUENCE = "query_sequence";
    private final static String ALIGNMENT_LENGTH = "alignment_length";
    private final static String TARGET_ALIGNMENTS = "target_alignments";
    private final static String TARGET_ID = "target_id";
    private final static String TARGET_SEQUENCE = "target_sequence";
    private final static String COVERAGE = "coverage";
    private final static String ALIGNED_REGIONS = "aligned_regions";
    private final static String ALIGNMENT_LOGO = "alignment_logo";
    private final static String VALUE = "value";
    private final static String SYMBOL = "symbol";
    /*
    * Why next attributes are not defined quoting strings ?
    * The main difference is that these attributes (getters and setters) are defined in mojave models classes:
    * org.rcsb.mojave.auto.Coverage and org.rcsb.mojave.auto.AlignedRegions.
    * For that reason, we use the same DW schema attribute names accessible from org.rcsb.mojave.SequenceCoordinatesConstants
    * instead of defining them here.
    * */
    private final static String QUERY_COVERAGE = SequenceCoordinatesConstants.QUERY_COVERAGE;
    private final static String TARGET_COVERAGE = SequenceCoordinatesConstants.TARGET_COVERAGE;
    private final static String QUERY_LENGTH = SequenceCoordinatesConstants.QUERY_LENGTH;
    private final static String TARGET_LENGTH = SequenceCoordinatesConstants.TARGET_LENGTH;
    private final static String QUERY_BEGIN = SequenceCoordinatesConstants.QUERY_BEGIN;
    private final static String QUERY_END = SequenceCoordinatesConstants.QUERY_END;
    private final static String TARGET_BEGIN = SequenceCoordinatesConstants.TARGET_BEGIN;
    private final static String TARGET_END = SequenceCoordinatesConstants.TARGET_END;
    private final static String FEATURES = SequenceCoordinatesConstants.FEATURES;
    private final static String FEATURE_POSITIONS = SequenceCoordinatesConstants.FEATURE_POSITIONS;
    private final static String ADDITIONAL_PROPERTIES = SequenceCoordinatesConstants.ADDITIONAL_PROPERTIES;
    private final static String PROPERTY_NAME = SequenceCoordinatesConstants.PROPERTY_NAME;
    private final static String PROPERTY_VALUE = SequenceCoordinatesConstants.PROPERTY_VALUE;
    private final static String TYPE = SequenceCoordinatesConstants.TYPE;
    private final static String NAME = SequenceCoordinatesConstants.NAME;
    private final static String BEG_SEQ_ID = SequenceCoordinatesConstants.BEG_SEQ_ID;
    private final static String END_SEQ_ID = SequenceCoordinatesConstants.END_SEQ_ID;
    private final static String BEG_ORI_ID = SequenceCoordinatesConstants.BEG_ORI_ID;
    private final static String END_ORI_ID = SequenceCoordinatesConstants.END_ORI_ID;
    private final static String VALUES = SequenceCoordinatesConstants.VALUES;
    private final static String OPEN_BEGIN = SequenceCoordinatesConstants.OPEN_BEGIN;
    private final static String OPEN_END = SequenceCoordinatesConstants.OPEN_END;
    private final static String EXON_SHIFT = SequenceCoordinatesConstants.EXON_SHIFT;
    private final static String ORIENTATION = SequenceCoordinatesConstants.ORIENTATION;

    public static class Query {
        public final static String ALIGNMENTS = SchemaConstants.ALIGNMENTS;
        public final static String GROUP_ALIGNMENTS = SchemaConstants.GROUP_ALIGNMENTS;
        public final static String ANNOTATIONS = SchemaConstants.ANNOTATIONS;
        public final static String GROUP_ANNOTATIONS = SchemaConstants.GROUP_ANNOTATIONS;
        public final static String GROUP_ANNOTATIONS_SUMMARY = SchemaConstants.GROUP_ANNOTATIONS_SUMMARY;
    }

    public static class Subscription {
        public final static String ALIGNMENTS_SUBSCRIPTION = SchemaConstants.ALIGNMENTS_SUBSCRIPTION;
        public final static String GROUP_ALIGNMENTS_SUBSCRIPTION = SchemaConstants.GROUP_ALIGNMENTS_SUBSCRIPTION;
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
        public final static String TARGET_ALIGNMENTS = SchemaConstants.TARGET_ALIGNMENTS;
        public final static String TARGET_ID = SchemaConstants.TARGET_ID;
        public final static String TARGET_SEQUENCE = SchemaConstants.TARGET_SEQUENCE;
        public final static String COVERAGE = SchemaConstants.COVERAGE;
        public final static String ALIGNED_REGIONS = SchemaConstants.ALIGNED_REGIONS;
        public final static String ALIGNMENT_LOGO = SchemaConstants.ALIGNMENT_LOGO;
        public final static String VALUE = SchemaConstants.VALUE;
        public final static String SYMBOL = SchemaConstants.SYMBOL;
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
        public final static String ADDITIONAL_PROPERTIES = SchemaConstants.ADDITIONAL_PROPERTIES;
        public final static String PROPERTY_NAME = SchemaConstants.PROPERTY_NAME;
        public final static String PROPERTY_VALUE = SchemaConstants.PROPERTY_VALUE;
        public final static String TYPE = SchemaConstants.TYPE;
        public final static String NAME = SchemaConstants.NAME;
        public final static String BEG_SEQ_ID = SchemaConstants.BEG_SEQ_ID;
        public final static String END_SEQ_ID = SchemaConstants.END_SEQ_ID;
        public final static String BEG_ORI_ID = SchemaConstants.BEG_ORI_ID;
        public final static String OPEN_BEGIN = SchemaConstants.OPEN_BEGIN;
        public final static String OPEN_END = SchemaConstants.OPEN_END;
        public final static String END_ORI_ID = SchemaConstants.END_ORI_ID;
        public final static String VALUES = SchemaConstants.VALUES;
        public final static String SOURCE = SchemaConstants.SOURCE;
        public final static String RANGE_ID = SchemaConstants.RANGE_ID;
        public final static String EXON_SHIFT = SchemaConstants.EXON_SHIFT;
        public final static String ORIENTATION = SchemaConstants.ORIENTATION;
    }

}
