package org.rcsb.graphqlschema.queries;

public interface SequenceAlignmentsQuery<X> {
    X alignments(String queryId);
}
