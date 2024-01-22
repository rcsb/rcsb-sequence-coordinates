package org.rcsb.collectors;

import org.bson.conversions.Bson;
import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.CoreConstants;

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
    public static String getGroupCollection(){
        return MongoCollections.COLL_SEQUENCE_COORDINATES_SEQUENCE_IDENTITY_GROUP_ALIGNMENTS;
    }
    public static String getGroupIndex(){
        return CoreConstants.QUERY_ID;
    }
    public static Bson alignmentFields() {
        return project(fields(
                include(CoreConstants.TARGET_ID),
                include(CoreConstants.ALIGNED_REGIONS),
                include(CoreConstants.SCORES),
                excludeId()
        ));
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
