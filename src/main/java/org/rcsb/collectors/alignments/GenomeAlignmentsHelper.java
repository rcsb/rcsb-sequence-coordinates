/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.utils.AlignmentRange;
import org.rcsb.utils.Range;

import java.util.List;
import java.util.Map;

import static org.rcsb.collectors.alignments.AlignmentsMongoHelper.getQueryIndex;
import static org.rcsb.utils.RangeMethods.*;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/26/24, Monday
 **/
public class GenomeAlignmentsHelper {

    public static Document mergeProteinToGenomeRegions(Document proteinAlignment, Document genomeAlignment){
        List<Document> alignedRegions = mergeAlignmentRegions(
                proteinAlignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class),
                genomeAlignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class)
        );
        return new Document(Map.of(
                SchemaConstants.Field.TARGET_ID, genomeAlignment.get(getQueryIndex()),
                SchemaConstants.Field.ORIENTATION, getIncrement(genomeAlignment),
                SchemaConstants.Field.ALIGNED_REGIONS, indexAlignedRegions(alignedRegions, genomeAlignment),
                SchemaConstants.Field.COVERAGE, computeCoverage(
                        proteinAlignment.get(SchemaConstants.Field.COVERAGE, Document.class),
                        genomeAlignment.get(SchemaConstants.Field.COVERAGE, Document.class),
                        alignedRegions
                )
        ));
    }

    private static List<Document> mergeAlignmentRegions(List<Document> proteinRegions, List<Document> genomeRegions){
        return proteinRegions.stream()
                .flatMap(proteinRegion -> proteinRegionIntersection(proteinRegion, genomeRegions).stream())
                .filter(d -> ! d.isEmpty())
                .toList();
    }

    private static List<Document> proteinRegionIntersection(Document proteinRegion, List<Document> genomeRegions){
        return genomeRegions.stream()
                .map( genomeRegion -> mapProteinToGenome(proteinRegion, genomeRegion) )
                .toList();
    }

    private static Document mapProteinToGenome(Document proteinRegion, Document genomeRegion){
        AlignmentRange proteinRange = buildAlignmentRange(proteinRegion);
        AlignmentRange genomeRange = buildAlignmentRange(genomeRegion);
        Range intersection = intersection(proteinRange.targetRange(), genomeRange.targetRange());
        if(intersection.isEmpty())
            return new Document();

        int queryBegin = mapIndex(intersection.bottom(), proteinRange.targetRange(), proteinRange.queryRange());
        int queryEnd = mapIndex(intersection.top(), proteinRange.targetRange(), proteinRange.queryRange());
        boolean includeExonShift = queryEnd == genomeRange.targetRange().top();
        List<Integer> exonShift = includeExonShift ? genomeRegion.getList(SchemaConstants.Field.EXON_SHIFT, Integer.class) : List.of();
        int targetBegin = mapToGenomeIndex(intersection.bottom(), genomeRange.targetRange(), genomeRange.queryRange()).get(0);
        int targetEnd = mapToGenomeIndex(intersection.top(), genomeRange.targetRange(), genomeRange.queryRange()).get(2 - exonShift.size());

        return new Document(Map.of(
                SchemaConstants.Field.QUERY_BEGIN, queryBegin,
                SchemaConstants.Field.QUERY_END, queryEnd,
                SchemaConstants.Field.TARGET_BEGIN, targetBegin,
                SchemaConstants.Field.TARGET_END, targetEnd,
                SchemaConstants.Field.EXON_SHIFT, exonShift
        ));
    }



    private static AlignmentRange buildAlignmentRange(Document alignment){
        return new AlignmentRange(
                new Range(alignment.getInteger(SchemaConstants.Field.QUERY_BEGIN), alignment.getInteger(SchemaConstants.Field.QUERY_END)),
                new Range(alignment.getInteger(SchemaConstants.Field.TARGET_BEGIN), alignment.getInteger(SchemaConstants.Field.TARGET_END))
        );
    }

    private static Document computeCoverage(Document proteinCoverage, Document genomeCoverage, List<Document> alignedRegions){
        int geneLength = genomeCoverage.getInteger(SchemaConstants.Field.QUERY_LENGTH);
        int proteinLength = proteinCoverage.getInteger(SchemaConstants.Field.QUERY_LENGTH);
        int alignmentProteinLength = extractProteinLength(
                alignedRegions
        );
        int alignmentGeneLength = extractGeneLength(
                alignedRegions
        );
        return new Document(Map.of(
                SchemaConstants.Field.QUERY_LENGTH, proteinLength,
                SchemaConstants.Field.TARGET_LENGTH, geneLength,
                SchemaConstants.Field.QUERY_COVERAGE, (double)alignmentProteinLength/ proteinLength,
                SchemaConstants.Field.TARGET_COVERAGE, (double)alignmentGeneLength/ geneLength
        ));
    }

    private static int extractGeneLength(List<Document> genomeRegions){
        return genomeRegions.stream().map(
                d -> d.getInteger(SchemaConstants.Field.TARGET_END) - d.getInteger(SchemaConstants.Field.TARGET_BEGIN) + 1 + (d.containsKey(SchemaConstants.Field.EXON_SHIFT) ? d.getList(SchemaConstants.Field.EXON_SHIFT, Integer.class).size() : 0)
        ).reduce(0, Integer::sum);
    }

    private static int extractProteinLength(List<Document> genomeRegions){
        return genomeRegions.stream().map(
                d -> d.getInteger(SchemaConstants.Field.QUERY_END) - d.getInteger(SchemaConstants.Field.QUERY_BEGIN) + 1
        ).reduce(0, Integer::sum);
    }

    private static List<Document> indexAlignedRegions(List<Document> alignedRegions, Document genomeAlignment){
        int genomeBegin = getStartPosition(genomeAlignment);
        int increment = getIncrement(genomeAlignment);
        return alignedRegions.stream().map(
                region -> indexRegion(region, genomeBegin, increment)
        ).toList();
    }

    private static Document indexRegion(Document region, int genomeBegin, int increment){
        return new Document(Map.of(
                SchemaConstants.Field.TARGET_BEGIN, indexPosition(region.getInteger(SchemaConstants.Field.TARGET_BEGIN), genomeBegin, increment),
                SchemaConstants.Field.TARGET_END, indexPosition(region.getInteger(SchemaConstants.Field.TARGET_END), genomeBegin, increment),
                SchemaConstants.Field.QUERY_BEGIN, region.getInteger(SchemaConstants.Field.QUERY_BEGIN),
                SchemaConstants.Field.QUERY_END, region.getInteger(SchemaConstants.Field.QUERY_END),
                SchemaConstants.Field.EXON_SHIFT, indexExonShift(region.getList(SchemaConstants.Field.EXON_SHIFT, Integer.class), genomeBegin, increment)
        ));
    }

    private static int indexPosition(int position, int genomeBegin, int increment){
        return (position - 1) * increment  + genomeBegin;
    }

    private static List<Integer> indexExonShift(List<Integer> exonShift, int genomeBegin, int increment){
        return exonShift.stream().map(
                n->indexPosition(n, genomeBegin, increment)
        ).toList();
    }

    private static int getStartPosition(Document genomeAlignment){
        if(genomeAlignment.getInteger(SchemaConstants.Field.ORIENTATION).equals(1))
            return genomeAlignment.getInteger(SchemaConstants.Field.QUERY_BEGIN);
        if(genomeAlignment.getInteger(SchemaConstants.Field.ORIENTATION).equals(2))
            return genomeAlignment.getInteger(SchemaConstants.Field.QUERY_END);
        throw new RuntimeException(String.format(
                "Unknown orientation value %s",
                genomeAlignment.getInteger(SchemaConstants.Field.ORIENTATION)
        ));
    }

    private static int getIncrement(Document genomeAlignment){
        if(genomeAlignment.getInteger(SchemaConstants.Field.ORIENTATION).equals(1))
            return 1;
        if(genomeAlignment.getInteger(SchemaConstants.Field.ORIENTATION).equals(2))
            return -1;
        throw new RuntimeException(String.format(
                "Unknown orientation value %s",
                genomeAlignment.getInteger(SchemaConstants.Field.ORIENTATION)
        ));
    }

    private static List<Integer> mapToGenomeIndex(int proteinIndex, Range proteinRange, Range genomeRange){
        if(!proteinRange.contains(proteinIndex))
            return List.of();
        int begin = 3 * (proteinIndex - proteinRange.bottom()) + genomeRange.bottom();
        return List.of(begin, begin + 1, begin + 2);
    }

}
