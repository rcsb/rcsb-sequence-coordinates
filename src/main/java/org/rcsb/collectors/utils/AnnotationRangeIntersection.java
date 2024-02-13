/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.utils;

import org.bson.Document;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.utils.Range;

import java.util.List;
import java.util.Map;

import static org.rcsb.utils.RangeMethods.intersection;
import static org.rcsb.utils.RangeMethods.mapIndex;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/6/24, Tuesday
 **/
public class AnnotationRangeIntersection implements RangeIntersection {

    @Override
    public List<Document> getRegions(Document annotation) {
        return annotation.getList(SchemaConstants.Field.FEATURES, Document.class).stream()
                .flatMap(feature->feature.getList(SchemaConstants.Field.FEATURE_POSITIONS, Document.class).stream())
                .toList();
    }

    @Override
    public int getRegionBegin(Document region) {
        return region.getInteger(SchemaConstants.Field.BEG_SEQ_ID);
    }

    @Override
    public int getRegionEnd(Document region) {
        if(region.containsKey(SchemaConstants.Field.VALUES))
            return region.getInteger(SchemaConstants.Field.BEG_SEQ_ID) + region.getList(SchemaConstants.Field.VALUES, Double.class).size() -1;
        return region.getInteger(SchemaConstants.Field.END_SEQ_ID);
    }

    @Override
    public Document applyRange(Range range, Document annotation) {
        annotation.put(
                SchemaConstants.Field.FEATURES,
                applyRangeToFeatures(range, annotation)
        );
        return annotation;
    }

    private List<Document> applyRangeToFeatures(Range range, Document annotation){
        return annotation.getList(SchemaConstants.Field.FEATURES, Document.class).stream()
                .map(feature -> applyRangeToFeature(range, feature))
                .filter(feature -> !feature.isEmpty())
                .toList();
    }

    private Document applyRangeToFeature(Range range, Document feature){
        List<Document> featurePositions = applyRangeToFeaturePositions(range, feature);
        if(featurePositions.isEmpty())
            return new Document();
        feature.put(
                SchemaConstants.Field.FEATURE_POSITIONS,
                featurePositions
        );
        return feature;
    }

    private List<Document> applyRangeToFeaturePositions(Range range, Document feature){
        return feature.getList(SchemaConstants.Field.FEATURE_POSITIONS, Document.class).stream()
                .map(position -> applyRangeToPosition(range, position))
                .filter(position -> !position.isEmpty())
                .toList();
    }

    private Document applyRangeToPosition(Range range, Document position) {
        if (position.containsKey(SchemaConstants.Field.VALUES))
            return valuesIntersection(range, position);
        return positionIntersection(range, position);
    }

    private Document positionIntersection(Range range, Document position){
        Range featureRange =  new Range(position.getInteger(SchemaConstants.Field.BEG_SEQ_ID), position.getInteger(SchemaConstants.Field.END_SEQ_ID));
        Range oriRange =  new Range(position.getInteger(SchemaConstants.Field.BEG_ORI_ID), position.getInteger(SchemaConstants.Field.END_ORI_ID));
        Range intersection = intersection(range, featureRange);
        if(intersection.isEmpty())
            return new Document();
        return new Document(Map.of(
                SchemaConstants.Field.BEG_SEQ_ID, intersection.bottom(),
                SchemaConstants.Field.END_SEQ_ID, intersection.top(),
                SchemaConstants.Field.BEG_ORI_ID, mapIndex(intersection.bottom(), featureRange, oriRange),
                SchemaConstants.Field.END_ORI_ID, mapIndex(intersection.top(), featureRange, oriRange)
        ));
    }

    private Document valuesIntersection(Range range, Document position){
        List<Double> positionValues = position.getList(SchemaConstants.Field.VALUES, Double.class);
        Range featureRange =  new Range(
                position.getInteger(SchemaConstants.Field.BEG_SEQ_ID),
                position.getInteger(SchemaConstants.Field.BEG_SEQ_ID) + positionValues.size() -1
        );
        Range oriRange =  new Range(
                position.getInteger(SchemaConstants.Field.BEG_ORI_ID),
                position.getInteger(SchemaConstants.Field.BEG_ORI_ID) + positionValues.size() -1
        );
        Range intersection = intersection(range, featureRange);
        if(intersection.isEmpty())
            return new Document();
        return new Document(Map.of(
                SchemaConstants.Field.BEG_SEQ_ID, intersection.bottom(),
                SchemaConstants.Field.BEG_ORI_ID, mapIndex(intersection.bottom(), featureRange, oriRange),
                SchemaConstants.Field.VALUES, positionValues.subList(
                        intersection.bottom() - featureRange.bottom(),
                        intersection.bottom() - featureRange.bottom() + intersection.size()
                )
        ));
    }

}