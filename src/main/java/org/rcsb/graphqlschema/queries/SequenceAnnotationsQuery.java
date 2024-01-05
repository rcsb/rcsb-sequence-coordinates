package org.rcsb.graphqlschema.queries;

public interface SequenceAnnotationsQuery<X> {
    X annotations(String queryId);
}
