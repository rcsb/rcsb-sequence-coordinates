/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.utils;

import org.bson.Document;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.utils.Range;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.rcsb.utils.RangeMethods.intersection;
import static org.rcsb.utils.RangeMethods.mapIndex;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/6/24, Tuesday
 **/
public class AlignmentRangeIntersection implements RangeIntersection {

    @Override
    public List<Document> getRegions(Document alignment) {
        return alignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class);
    }

    @Override
    public int getRegionBegin(Document region) {
        return region.getInteger(SchemaConstants.Field.QUERY_BEGIN);
    }

    @Override
    public int getRegionEnd(Document region) {
        return region.getInteger(SchemaConstants.Field.QUERY_END);
    }

    @Override
    public Document applyRange(Range range, Document alignment) {
        alignment.put(
                SchemaConstants.Field.ALIGNED_REGIONS,
                applyRangeToRegions(range, alignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class))
        );
        alignment.put(
                SchemaConstants.Field.COVERAGE,
                updateCoverage(alignment)
        );
        return alignment;

    }

    private List<Document> applyRangeToRegions(Range range, List<Document> regions){
        return regions.stream()
                .map(region -> applyRangeToRegion(range, region))
                .filter(d->!d.isEmpty())
                .collect(Collectors.toList());
    }

    private Document applyRangeToRegion(Range range, Document region){
        Range queryRange = new Range(region.getInteger(SchemaConstants.Field.QUERY_BEGIN), region.getInteger(SchemaConstants.Field.QUERY_END));
        Range targetRange = new Range(region.getInteger(SchemaConstants.Field.TARGET_BEGIN), region.getInteger(SchemaConstants.Field.TARGET_END));
        Range intersection = intersection(range, queryRange);
        if(intersection.isEmpty())
            return new Document();
        return new Document(Map.of(
                SchemaConstants.Field.QUERY_BEGIN, intersection.bottom(),
                SchemaConstants.Field.QUERY_END, intersection.top(),
                SchemaConstants.Field.TARGET_BEGIN, mapIndex(intersection.bottom(), queryRange, targetRange),
                SchemaConstants.Field.TARGET_END, mapIndex(intersection.top(), queryRange, targetRange)
        ));
    }

    private Document updateCoverage(Document alignment){
        Document coverage = alignment.get(SchemaConstants.Field.COVERAGE, Document.class);
        int alignmentLength = extracAlignmentLength(alignment);
        coverage.put(
                SchemaConstants.Field.QUERY_COVERAGE,
                (float) alignmentLength / coverage.getInteger(SchemaConstants.Field.QUERY_LENGTH)
        );
        coverage.put(
                SchemaConstants.Field.TARGET_COVERAGE,
                (float) alignmentLength / coverage.getInteger(SchemaConstants.Field.TARGET_LENGTH)
        );
        return coverage;
    }

    private int extracAlignmentLength(Document alignment){
        return alignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class).stream()
                .map(d->d.getInteger(SchemaConstants.Field.QUERY_END)-d.getInteger(SchemaConstants.Field.QUERY_BEGIN))
                .reduce(0, Integer::sum);
    }

}
