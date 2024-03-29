/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.service;

import org.rcsb.graphqlschema.params.AnnotationFilter;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public interface AnnotationsSubscription<X> {
    X annotationsSubscription(
            String queryId,
            SequenceReference reference,
            List<AnnotationReference> annotationReferences,
            List<AnnotationFilter> annotationFilters,
            List<Integer> range
    );
    X groupAnnotationsSubscription(
            String groupId,
            GroupReference group,
            List<AnnotationReference> annotationReferences,
            List<AnnotationFilter> annotationFilters
    );
}
