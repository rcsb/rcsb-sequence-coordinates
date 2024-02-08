/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.service;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public interface AlignmentsQuery<X> {
    X alignment(
            String queryId,
            SequenceReference from,
            SequenceReference to,
            List<Integer> range
    );

    X groupAlignment(
            String groupId,
            GroupReference group,
            List<String> filter
    );
}
