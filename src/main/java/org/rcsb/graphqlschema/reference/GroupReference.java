/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.reference;

import org.rcsb.mojave.enumeration.RcsbGroupAggregationMethodType;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public enum GroupReference {
    MATCHING_UNIPROT_ACCESSION(RcsbGroupAggregationMethodType.MATCHING_UNIPROT_ACCESSION.value().toUpperCase()),
    SEQUENCE_IDENTITY(RcsbGroupAggregationMethodType.SEQUENCE_IDENTITY.value().toUpperCase());

    private final String value;
    GroupReference(String value) {
        this.value = value;
    }
    public String toString() {
        return this.value;
    }

    public SequenceReference toSequenceReference(){
        return SequenceReference.PDB_ENTITY;
    }
}
