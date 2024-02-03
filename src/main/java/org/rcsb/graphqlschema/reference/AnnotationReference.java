package org.rcsb.graphqlschema.reference;

import java.util.Objects;

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
