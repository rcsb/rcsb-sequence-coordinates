package org.rcsb.graphqlschema.reference;

import org.rcsb.mojave.enumeration.RcsbGroupAggregationMethodType;
public class GroupReference {
    public enum ReferenceName {
        MATCHING_UNIPROT_ACCESSION(RcsbGroupAggregationMethodType.MATCHING_UNIPROT_ACCESSION.value().toUpperCase()),
        SEQUENCE_IDENTITY(RcsbGroupAggregationMethodType.SEQUENCE_IDENTITY.value().toUpperCase());

        private final String value;
        ReferenceName(String value) {
            this.value = value;
        }
        public String toString() {
            return this.value;
        }
    }

}
