package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAnnotations;

import java.util.List;

public interface AnnotationsQuery<X> {
    X annotations(
            String queryId,
            SequenceReference.ReferenceName reference,
            List<SequenceAnnotations.Source> sources
    );
    X group_annotations(
            String groupId,
            GroupReference.ReferenceName group,
            List<SequenceAnnotations.Source> sources
    );
}
