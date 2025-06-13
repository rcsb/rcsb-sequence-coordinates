/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.utils;

import org.bson.Document;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.utils.Range;

import java.util.List;
import java.util.Map;

import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsReferenceHelper.testGenome;
import static org.rcsb.utils.RangeMethods.intersection;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/6/24, Tuesday
 **/
public class GenomeRangeIntersection implements RangeIntersection {

    private int orientation = 1;
    private final SequenceReference reference;

    public GenomeRangeIntersection(SequenceReference reference){
        this.reference = reference;
    }

    @Override
    public List<Document> getRegions(Document alignment) {
        setOrientation(alignment);
        return alignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class);
    }

    @Override
    public int getRegionBegin(Document region) {
        if(testGenome(reference) && orientation < 0)
            return region.getInteger(SchemaConstants.Field.QUERY_END);
        return region.getInteger(SchemaConstants.Field.QUERY_BEGIN);
    }

    @Override
    public int getRegionEnd(Document region) {
        if(testGenome(reference) && orientation < 0)
            return region.getInteger(SchemaConstants.Field.QUERY_BEGIN);
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
                .toList();
    }

    private Document applyRangeToRegion(Range range, Document region){
        Range queryRange = buildQueryRange(region);
        Range intersection = intersection(range, queryRange);
        if(intersection.isEmpty())
            return new Document();

        return new Document(Map.of(
                SchemaConstants.Field.QUERY_BEGIN, getQueryBegin(intersection),
                SchemaConstants.Field.QUERY_END, getQueryEnd(intersection),
                SchemaConstants.Field.TARGET_BEGIN, getTargetBegin(intersection, region),
                SchemaConstants.Field.TARGET_END, getTargetEnd(intersection, region),
                SchemaConstants.Field.EXON_SHIFT, queryRange.top() == intersection.top() ? region.get(SchemaConstants.Field.EXON_SHIFT) : List.of()
        ));
    }

    private int getQueryBegin(Range intersection){
        if(testGenome(reference) && orientation < 0)
           return intersection.top();
        return intersection.bottom();
    }

    private int getQueryEnd(Range intersection){
        if(testGenome(reference) && orientation < 0)
            return intersection.bottom();
        return intersection.top();
    }

    private int getTargetBegin(Range intersection,  Document region){
        Range queryRange = buildQueryRange(region);
        if(testGenome(reference))
            return region.getInteger(SchemaConstants.Field.TARGET_BEGIN) + getTargetBeginIncrement(intersection, queryRange);
        return region.getInteger(SchemaConstants.Field.TARGET_BEGIN) + (intersection.bottom() - queryRange.bottom()) * orientation * 3;
    }

    private int getTargetEnd(Range intersection,  Document region){
        Range queryRange = buildQueryRange(region);
        if(testGenome(reference))
            return region.getInteger(SchemaConstants.Field.TARGET_END) - getTargetEndIncrement(intersection, queryRange);
        return region.getInteger(SchemaConstants.Field.TARGET_END) - (queryRange.top() - intersection.top()) * orientation * 3;
    }

    private int getTargetBeginIncrement(Range intersection, Range queryRange){
        if(orientation > 0 )
            return (intersection.bottom() - queryRange.bottom()) / 3;
        return (queryRange.top() - intersection.top()) / 3;
    }

    private int getTargetEndIncrement(Range intersection, Range queryRange){
        if(orientation > 0 )
            return (queryRange.top() - intersection.top()) / 3;
        return (intersection.bottom() - queryRange.bottom()) / 3;
    }

    private Range buildQueryRange(Document region){
        if(testGenome(reference) && orientation < 0)
            return new Range(region.getInteger(SchemaConstants.Field.QUERY_END), region.getInteger(SchemaConstants.Field.QUERY_BEGIN));
        return new Range(region.getInteger(SchemaConstants.Field.QUERY_BEGIN), region.getInteger(SchemaConstants.Field.QUERY_END));
    }

    private Document updateCoverage(Document alignment){
        Document coverage = alignment.get(SchemaConstants.Field.COVERAGE, Document.class);
        int alignmentLength = extracProteinAlignmentLength(alignment);
        coverage.put(
                SchemaConstants.Field.QUERY_COVERAGE,
                (float) (testGenome(reference) ? 3 : 1) * alignmentLength / coverage.getInteger(SchemaConstants.Field.QUERY_LENGTH)
        );
        coverage.put(
                SchemaConstants.Field.TARGET_COVERAGE,
                (float) (testGenome(reference) ? 1 : 3) * alignmentLength / coverage.getInteger(SchemaConstants.Field.TARGET_LENGTH)
        );
        return coverage;
    }

    private int extracProteinAlignmentLength(Document alignment){
        return alignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class).stream()
                .map(d->d.getInteger(getProteinEndAttribute()) - d.getInteger(getProteinBeginAttribute()) + 1)
                .reduce(0, Integer::sum);
    }

    private void setOrientation(Document alignment){
        if(alignment.containsKey(SchemaConstants.Field.ORIENTATION))
            orientation = alignment.getInteger(SchemaConstants.Field.ORIENTATION);
    }

    private String getProteinBeginAttribute(){
        if(testGenome(reference))
            return SchemaConstants.Field.TARGET_BEGIN;
        return SchemaConstants.Field.QUERY_BEGIN;
    }

    private String getProteinEndAttribute(){
        if(testGenome(reference))
            return SchemaConstants.Field.TARGET_END;
        return SchemaConstants.Field.QUERY_END;
    }

}
