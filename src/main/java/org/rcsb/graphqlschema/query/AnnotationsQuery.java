/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public interface AnnotationsQuery<X> {
    X annotations(
            String queryId,
            SequenceReference reference,
            List<AnnotationReference> sources
    );
    X groupAnnotations(
            String groupId,
            GroupReference group,
            List<AnnotationReference> sources
    );
}
