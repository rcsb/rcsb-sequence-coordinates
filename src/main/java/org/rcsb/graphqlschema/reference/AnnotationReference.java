/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.reference;

import java.util.Objects;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public enum AnnotationReference {
    PDB_INSTANCE("PDB_INSTANCE"),
    PDB_ENTITY("PDB_ENTITY"),
    PDB_INTERFACE("PDB_INTERFACE"),
    UNIPROT("UNIPROT");

    private final String value;

    AnnotationReference(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public SequenceReference toSequenceReference(){
        if (Objects.equals(value, PDB_INTERFACE.value))
            return SequenceReference.PDB_INSTANCE;
        return SequenceReference.valueOf(value);
    }

}
