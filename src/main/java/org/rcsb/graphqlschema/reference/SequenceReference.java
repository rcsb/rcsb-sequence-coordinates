/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.reference;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public enum SequenceReference {
    PDB_INSTANCE("PDB_INSTANCE"),
    PDB_ENTITY("PDB_ENTITY"),
    NCBI_PROTEIN("NCBI_PROTEIN"),
    NCBI_GENOME("NCBI_GENOME"),
    UNIPROT("UNIPROT");

    private final String value;
    SequenceReference(String value) {
        this.value = value;
    }
    public String toString() {
        return this.value;
    }

}
