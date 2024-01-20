package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

public interface AlignmentsQuery<X> {
    X alignments(
            String queryId,
            SequenceReference from,
            SequenceReference to
    );

    X group_alignments(
            String groupId,
            GroupReference group
    );
}
