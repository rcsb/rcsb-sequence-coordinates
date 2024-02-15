/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.annotations;

import org.bson.Document;
import org.rcsb.graphqlschema.schema.SchemaConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/15/24, Thursday
 **/
public class AnnotationSummaryHelper {

    public static List<Document> mergeFeaturePositions(int alignmentLength, List<Document> features){
        return formatSummary(features.stream()
                .map(feature -> mapFeatureToSummary(alignmentLength, feature))
                .reduce( zeroSummary(alignmentLength), AnnotationSummaryHelper::mergeSummary)
        );
    }

    private static List<Document> formatSummary(Double[] summary){
        return List.of(new Document(Map.of(
                SchemaConstants.Field.BEG_SEQ_ID, 1,
                SchemaConstants.Field.VALUES, List.of(summary)
        )));
    }

    private static Double[] mapFeatureToSummary(int alignmentLength, Document feature){
        return feature.getList(SchemaConstants.Field.FEATURE_POSITIONS, Document.class).stream()
                .map(region -> mapRegionToSummary(alignmentLength, region))
                .reduce(zeroSummary(alignmentLength), AnnotationSummaryHelper::mergeSummary);
    }

    private static Double[] mapRegionToSummary(int alignmentLength, Document region){
        if(region.containsKey(SchemaConstants.Field.VALUES))
            return mapValuesToSummary(alignmentLength, region);
        Double[] summary = zeroSummary(alignmentLength);
        int begin = region.getInteger(SchemaConstants.Field.BEG_SEQ_ID);
        int end = region.getInteger(SchemaConstants.Field.END_SEQ_ID);
        for(int i=begin; i<=end; i++){
            summary[i-1] = 1.;
        }
        return summary;
    }

    private static Double[] mergeSummary(Double[] summaryA, Double[] summaryB){
        Double[] summary = zeroSummary(summaryA.length);
        for (int i=0; i<summaryA.length; i++)
            summary[i] = summaryA[i] + summaryB[i];
        return summary;
    }

    private static Double[] mapValuesToSummary(int alignmentLength, Document region){
        Double[] summary = zeroSummary(alignmentLength);
        AtomicInteger begin = new AtomicInteger(region.getInteger(SchemaConstants.Field.BEG_SEQ_ID) - 1);
        region.getList(SchemaConstants.Field.VALUES, Double.class).forEach(
                value -> summary[ begin.getAndIncrement() ] = value
        );
        return summary;
    }

    private static Double[] zeroSummary(int length){
        Double[] summary = new Double[length];
        Arrays.fill(summary, 0.);
        return summary;
    }

}
