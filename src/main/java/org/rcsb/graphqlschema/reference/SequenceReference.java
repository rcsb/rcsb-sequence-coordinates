package org.rcsb.graphqlschema.reference;

public class SequenceReference {
    public enum ReferenceName {
        PDB_INSTANCE("PDB_INSTANCE"),
        PDB_ENTITY("PDB_ENTITY"),
        NCBI_PROTEIN("NCBI_PROTEIN"),
        NCBI_GENOME("NCBI_GENOME"),
        UNIPROT("UNIPROT");

        private final String value;
        ReferenceName(String value) {
            this.value = value;
        }
        public String toString() {
            return this.value;
        }
    }

}
