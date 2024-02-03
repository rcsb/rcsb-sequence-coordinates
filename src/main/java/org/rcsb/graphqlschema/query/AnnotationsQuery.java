package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

import java.util.List;

public interface AnnotationsQuery<X> {
    X annotations(
            String queryId,
            SequenceReference reference,
            List<AnnotationReference> sources
    );
    X group_annotations(
            String groupId,
            GroupReference group,
            List<AnnotationReference> sources
    );
}
