/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.query;

import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public interface AlignmentsQuery<X> {
    X alignment(
            String queryId,
            SequenceReference from,
            SequenceReference to
    );

    X groupAlignment(
            String groupId,
            GroupReference group
    );
}
