/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.SequenceCoordinatesConstants;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.rcsb.collectors.alignments.AlignmentsMongoHelper.getIndex;


/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class AlignmentsDocumentHelper {

    public static Function<Document,Document> targetIdSelector(SequenceReference from, SequenceReference to){
        if(getIndex(from, to).equals(SequenceCoordinatesConstants.QUERY_ID))
            return (d) -> new Document(Map.of(
                    SchemaConstants.Field.TARGET_ID, d.get(SequenceCoordinatesConstants.TARGET_ID),
                    SchemaConstants.Field.COVERAGE, d.get(SequenceCoordinatesConstants.COVERAGE),
                    SchemaConstants.Field.ALIGNED_REGIONS, d.get(SequenceCoordinatesConstants.ALIGNED_REGIONS)
            ));
        if(getIndex(from, to).equals(SequenceCoordinatesConstants.TARGET_ID))
            return (d) -> new Document(Map.of(
                    SchemaConstants.Field.TARGET_ID, d.get(SequenceCoordinatesConstants.QUERY_ID),
                    SchemaConstants.Field.COVERAGE, switchCoverage(d.get(SequenceCoordinatesConstants.COVERAGE, Document.class)),
                    SchemaConstants.Field.ALIGNED_REGIONS, switchAlignedRegions(d.getList(SequenceCoordinatesConstants.ALIGNED_REGIONS, Document.class))
            ));
        throw new RuntimeException(
                String.format(
                        "Unknown Document Map for references from %s to %s",
                        from,
                        to
                )
        );
    }

    public static BiFunction<String, Document, Document> targetIdSubstitutor(SequenceReference reference){
        if(reference.equals(SequenceReference.PDB_INSTANCE))
            return (targetId, alignment) -> new Document(Map.of(
                    SchemaConstants.Field.TARGET_ID, targetId,
                    SchemaConstants.Field.COVERAGE, alignment.get(SequenceCoordinatesConstants.COVERAGE),
                    SchemaConstants.Field.ALIGNED_REGIONS, alignment.get(SequenceCoordinatesConstants.ALIGNED_REGIONS)
            ));
        return (targetId, alignment) -> alignment;
    }

    public static Document identityAlignment(String queryId, Integer length){
        return new Document(Map.of(
                SchemaConstants.Field.TARGET_ID, queryId,
                SchemaConstants.Field.COVERAGE, new Document(Map.of(
                        SchemaConstants.Field.QUERY_COVERAGE, 1.0,
                        SchemaConstants.Field.TARGET_COVERAGE, 1.0,
                        SchemaConstants.Field.QUERY_LENGTH, length,
                        SchemaConstants.Field.TARGET_LENGTH, length

                )),
                SchemaConstants.Field.ALIGNED_REGIONS, List.of(new Document(Map.of(
                        SchemaConstants.Field.QUERY_BEGIN, 1,
                        SchemaConstants.Field.QUERY_END, length,
                        SchemaConstants.Field.TARGET_BEGIN, 1,
                        SchemaConstants.Field.TARGET_END, length

                )))
        ));
    }

    public static Document switchAlignment(String targetId, Document alignment){
        Document reverseAlignment = new Document(Map.of(
                SchemaConstants.Field.TARGET_ID, targetId,
                SchemaConstants.Field.COVERAGE, switchCoverage(alignment.get(SequenceCoordinatesConstants.COVERAGE, Document.class)),
                SchemaConstants.Field.ALIGNED_REGIONS, switchAlignedRegions(alignment.getList(SequenceCoordinatesConstants.ALIGNED_REGIONS, Document.class))
        ));
        if(alignment.containsKey(SequenceCoordinatesConstants.ORIENTATION))
            reverseAlignment.put(SequenceCoordinatesConstants.ORIENTATION, alignment.get(SequenceCoordinatesConstants.ORIENTATION));
        return reverseAlignment;
    }

    private static Document switchCoverage(Document d){
        return new Document(Map.of(
                SchemaConstants.Field.QUERY_COVERAGE, d.get(SequenceCoordinatesConstants.TARGET_COVERAGE),
                SchemaConstants.Field.QUERY_LENGTH, d.get(SequenceCoordinatesConstants.TARGET_LENGTH),
                SchemaConstants.Field.TARGET_COVERAGE, d.get(SequenceCoordinatesConstants.QUERY_COVERAGE),
                SchemaConstants.Field.TARGET_LENGTH, d.get(SequenceCoordinatesConstants.QUERY_LENGTH)
        ));
    }

    private static List<Document> switchAlignedRegions(List<Document> documentList){
        return documentList.stream().map(d-> {
                Document region = new Document( Map.of(
                    SchemaConstants.Field.QUERY_BEGIN, d.get(SequenceCoordinatesConstants.TARGET_BEGIN),
                    SchemaConstants.Field.QUERY_END, d.get(SequenceCoordinatesConstants.TARGET_END),
                    SchemaConstants.Field.TARGET_BEGIN, d.get(SequenceCoordinatesConstants.QUERY_BEGIN),
                    SchemaConstants.Field.TARGET_END, d.get(SequenceCoordinatesConstants.QUERY_END)
                ));
                if(d.containsKey(SequenceCoordinatesConstants.EXON_SHIFT))
                    region.put(SchemaConstants.Field.EXON_SHIFT, d.get(SequenceCoordinatesConstants.EXON_SHIFT));
                return region;
        }).toList();
    }

}
