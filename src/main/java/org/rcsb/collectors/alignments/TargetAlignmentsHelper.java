package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.CoreConstants;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.*;

public class TargetAlignmentsHelper {

    public static String getCollection(SequenceReference from, SequenceReference to) {
        if(testUniprot(from, to) && testPdb(from, to))
            return MongoCollections.COLL_SEQUENCE_COORDINATES_UNIPROT_TO_PDB_ENTITY_ALIGNMENTS;
        if(testNcbi(from, to) && testPdb(from, to))
            return MongoCollections.COLL_SEQUENCE_COORDINATES_NCBI_TO_PDB_ENTITY_ALIGNMENTS;
        if(testNcbi(from, to) && testUniprot(from, to))
            return MongoCollections.COLL_SEQUENCE_COORDINATES_NCBI_TO_UNIPROT_ALIGNMENTS;
        throw new RuntimeException(
                String.format(
                        "Unknown mapping references from %s to %s",
                        from,
                        to
                )
        );
    }
    public static String getIndex(SequenceReference from, SequenceReference to){
        if(from.equals(SequenceReference.NCBI_PROTEIN))
            return CoreConstants.QUERY_ID;
        if(to.equals(SequenceReference.NCBI_PROTEIN))
            return CoreConstants.TARGET_ID;
        if(from.equals(SequenceReference.UNIPROT))
            return CoreConstants.QUERY_ID;
        if(to.equals(SequenceReference.UNIPROT))
            return CoreConstants.TARGET_ID;
        throw new RuntimeException(
                String.format(
                        "Unknown index for references from %s to %s",
                        from,
                        to
                )
        );
    }
    public static List<String> getSortFields(SequenceReference from, SequenceReference to){
        if(from.equals(SequenceReference.NCBI_PROTEIN))
            return List.of(CoreConstants.QUERY_BEGIN, CoreConstants.QUERY_END);
        if(to.equals(SequenceReference.NCBI_PROTEIN))
            return List.of(CoreConstants.TARGET_BEGIN, CoreConstants.TARGET_END);
        if(from.equals(SequenceReference.UNIPROT))
            return List.of(CoreConstants.QUERY_BEGIN, CoreConstants.QUERY_END);
        if(to.equals(SequenceReference.UNIPROT))
            return List.of(CoreConstants.TARGET_BEGIN, CoreConstants.TARGET_END);
        throw new RuntimeException(
                String.format(
                        "Unknown index for references from %s to %s",
                        from,
                        to
                )
        );
    }
    public static String getGroupCollection(){
        return MongoCollections.COLL_SEQUENCE_COORDINATES_SEQUENCE_IDENTITY_GROUP_ALIGNMENTS;
    }
    public static String getGroupIndex(){
        return CoreConstants.QUERY_ID;
    }
    public static List<String> getGroupSortFields(){
        return List.of(CoreConstants.QUERY_BEGIN, CoreConstants.QUERY_END);
    }
    public static Bson alignmentFields() {
        return project(fields(
                include(CoreConstants.TARGET_ID),
                include(CoreConstants.QUERY_ID),
                include(CoreConstants.ALIGNED_REGIONS),
                include(CoreConstants.COVERAGE),
                excludeId()
        ));
    }
    public static Function<Document,Document> getDocumentMap(SequenceReference from, SequenceReference to){
        if(getIndex(from, to).equals(CoreConstants.QUERY_ID))
            return(d) -> new Document(Map.of(
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
    private static Document switchCoverage(Document d){
        return new Document(Map.of(
                SchemaConstants.Field.QUERY_COVERAGE, d.get(CoreConstants.TARGET_COVERAGE),
                SchemaConstants.Field.QUERY_LENGTH, d.get(CoreConstants.TARGET_LENGTH),
                SchemaConstants.Field.TARGET_COVERAGE, d.get(CoreConstants.QUERY_COVERAGE),
                SchemaConstants.Field.TARGET_LENGTH, d.get(CoreConstants.QUERY_LENGTH)
        ));
    }
    private static List<Document> switchAlignedRegions(List<Document> documentList){
        return documentList.stream().map(
                d-> new Document( Map.of(
                        SchemaConstants.Field.QUERY_BEGIN, d.get(CoreConstants.TARGET_BEGIN),
                        SchemaConstants.Field.QUERY_END, d.get(CoreConstants.TARGET_END),
                        SchemaConstants.Field.TARGET_BEGIN, d.get(CoreConstants.QUERY_BEGIN),
                        SchemaConstants.Field.TARGET_END, d.get(CoreConstants.QUERY_END)
                ))
        ).collect(Collectors.toList());
    }
    private static boolean testUniprot(SequenceReference from, SequenceReference to){
        return from.equals(SequenceReference.UNIPROT) || to.equals(SequenceReference.UNIPROT);
    }
    private static boolean testNcbi(SequenceReference from, SequenceReference to){
        return from.equals(SequenceReference.NCBI_PROTEIN) || to.equals(SequenceReference.NCBI_PROTEIN);
    }
    private static boolean testPdb(SequenceReference from, SequenceReference to){
        return from.equals(SequenceReference.PDB_ENTITY) || to.equals(SequenceReference.PDB_ENTITY);
    }

}
