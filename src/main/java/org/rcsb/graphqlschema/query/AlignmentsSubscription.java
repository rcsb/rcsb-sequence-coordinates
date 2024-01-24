package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

public interface AlignmentsSubscription<X> {
    X alignment_subscription(
            String queryId,
            SequenceReference from,
            SequenceReference to
    );

    X group_alignment_subscription(
            String groupId,
            GroupReference group
    );
}
