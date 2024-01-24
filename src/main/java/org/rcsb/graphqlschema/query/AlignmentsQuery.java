package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

public interface AlignmentsQuery<X> {
    X alignment(
            String queryId,
            SequenceReference from,
            SequenceReference to
    );

    X group_alignment(
            String groupId,
            GroupReference group
    );
}
