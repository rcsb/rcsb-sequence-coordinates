package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference.ReferenceName;

public interface AlignmentsQuery<X> {
    X alignments(
            String queryId,
            ReferenceName from,
            ReferenceName to
    );

    X group_alignments(
            String groupId,
            GroupReference.ReferenceName group
    );
}
