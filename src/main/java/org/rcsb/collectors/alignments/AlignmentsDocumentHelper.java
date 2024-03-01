/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.CoreConstants;

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
        if(getIndex(from, to).equals(CoreConstants.QUERY_ID))
            return (d) -> new Document(Map.of(
                    SchemaConstants.Field.TARGET_ID, d.get(CoreConstants.TARGET_ID),
                    SchemaConstants.Field.COVERAGE, d.get(CoreConstants.COVERAGE),
                    SchemaConstants.Field.ALIGNED_REGIONS, d.get(CoreConstants.ALIGNED_REGIONS)
            ));
        if(getIndex(from, to).equals(CoreConstants.TARGET_ID))
            return (d) -> new Document(Map.of(
                    SchemaConstants.Field.TARGET_ID, d.get(CoreConstants.QUERY_ID),
                    SchemaConstants.Field.COVERAGE, switchCoverage(d.get(CoreConstants.COVERAGE, Document.class)),
                    SchemaConstants.Field.ALIGNED_REGIONS, switchAlignedRegions(d.getList(CoreConstants.ALIGNED_REGIONS, Document.class))
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
                    SchemaConstants.Field.COVERAGE, alignment.get(CoreConstants.COVERAGE),
                    SchemaConstants.Field.ALIGNED_REGIONS, alignment.get(CoreConstants.ALIGNED_REGIONS)
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
                SchemaConstants.Field.COVERAGE, switchCoverage(alignment.get(CoreConstants.COVERAGE, Document.class)),
                SchemaConstants.Field.ALIGNED_REGIONS, switchAlignedRegions(alignment.getList(CoreConstants.ALIGNED_REGIONS, Document.class))
        ));
        if(alignment.containsKey(CoreConstants.ORIENTATION))
            reverseAlignment.put(CoreConstants.ORIENTATION, alignment.get(CoreConstants.ORIENTATION));
        return reverseAlignment;
    }

    private static Document switchCoverage(Document d){
        return new Document(Map.of(
                SchemaConstants.Field.QUERY_COVERAGE, d.get(CoreConstants.TARGET_COVERAGE),
                SchemaConstants.Field.QUERY_LENGTH, d.get(CoreConstants.TARGET_LENGTH),
                SchemaConstants.Field.TARGET_COVERAGE, d.get(CoreConstants.QUERY_COVERAGE),
                SchemaConstants.Field.TARGET_LENGTH, d.get(CoreConstants.QUERY_LENGTH)
        ));
    }

    private static List<Document> switchAlignedRegions(List<Document> documentList){
        return documentList.stream().map(d-> {
                Document region = new Document( Map.of(
                    SchemaConstants.Field.QUERY_BEGIN, d.get(CoreConstants.TARGET_BEGIN),
                    SchemaConstants.Field.QUERY_END, d.get(CoreConstants.TARGET_END),
                    SchemaConstants.Field.TARGET_BEGIN, d.get(CoreConstants.QUERY_BEGIN),
                    SchemaConstants.Field.TARGET_END, d.get(CoreConstants.QUERY_END)
                ));
                if(d.containsKey(CoreConstants.EXON_SHIFT))
                    region.put(SchemaConstants.Field.EXON_SHIFT, d.get(CoreConstants.EXON_SHIFT));
                return region;
        }).toList();
    }

}
