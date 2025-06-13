/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.annotations;

import org.bson.Document;
import org.rcsb.graphqlschema.schema.SchemaConstants;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/15/24, Thursday
 **/
public class AnnotationSummaryHelper {

    public static List<Document> mergeFeaturePositions(int alignmentLength, List<Document> features){
        double[] summary = new double[alignmentLength];
        features.forEach(feature -> mapFeatureToSummary(summary, feature));
        return formatSummary(summary);
    }

    private static List<Document> formatSummary(double[] summary){
        return IntStream.range(0, summary.length)
                .filter(i -> summary[i] != 0.0)
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> IntStream.range(0, i)
                                .filter(j -> summary[j] == 0.0)
                                .reduce((first, second) -> second)
                                .orElse(-1)
                ))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> {
                    List<Double> values = entry.getValue().stream()
                            .map(i -> summary[i])
                            .toList();
                    return new Document(Map.of(
                            SchemaConstants.Field.BEG_SEQ_ID, entry.getKey() + 2,
                            SchemaConstants.Field.VALUES, values
                    ));
                })
                .collect(Collectors.toList());
    }

    private static void mapFeatureToSummary(double[] summary, Document feature){
        feature.getList(SchemaConstants.Field.FEATURE_POSITIONS, Document.class).forEach(
                region-> mapRegionToSummary(summary, region)
        );
    }

    private static void mapRegionToSummary(double[] summary, Document region){
        if(region.containsKey(SchemaConstants.Field.VALUES))
            mapValuesToSummary(summary, region);
        int begin = region.getInteger(SchemaConstants.Field.BEG_SEQ_ID);
        int end = region.getInteger(SchemaConstants.Field.END_SEQ_ID);
        for(int i=begin; i<=end; i++){
            summary[i-1] += 1.;
        }
    }

    private static void mapValuesToSummary(double[] summary, Document region){
        AtomicInteger begin = new AtomicInteger(region.getInteger(SchemaConstants.Field.BEG_SEQ_ID) - 1);
        region.getList(SchemaConstants.Field.VALUES, Double.class).forEach(
                value -> summary[ begin.getAndIncrement() ] += value
        );
    }

}
