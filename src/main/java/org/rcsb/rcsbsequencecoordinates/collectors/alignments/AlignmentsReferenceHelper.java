/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.alignments;

import org.rcsb.graphqlschema.reference.SequenceReference;

/**
 * @author : joan
 */
public class AlignmentsReferenceHelper {

    public static boolean equivalentReferences(SequenceReference from, SequenceReference to){
        if(from.equals(to))
            return true;
        if(testPdb(from) && testPdb(to))
            return true;
        return false;
    }

    public static boolean testUniprot(SequenceReference from, SequenceReference to){
        return testUniprot(from) || testUniprot(to);
    }

    public static boolean testNcbi(SequenceReference from, SequenceReference to){
        return testNcbi(from) || testNcbi(to);
    }

    public static boolean testPdb(SequenceReference from, SequenceReference to){
        return testPdb(from) || testPdb(to);
    }

    public static boolean testGenome(SequenceReference from, SequenceReference to) {
        return testGenome(from) || testGenome(to);
    }

    public static boolean testUniprot(SequenceReference reference){
        return reference.equals(SequenceReference.UNIPROT);
    }

    public static boolean testNcbi(SequenceReference reference){
        return reference.equals(SequenceReference.NCBI_PROTEIN);
    }

    public static boolean testPdb(SequenceReference reference){
        return reference.equals(SequenceReference.PDB_ENTITY) || reference.equals(SequenceReference.PDB_INSTANCE);
    }

    public static boolean testGenome(SequenceReference reference) {
        return reference.equals(SequenceReference.NCBI_GENOME);
    }

}
