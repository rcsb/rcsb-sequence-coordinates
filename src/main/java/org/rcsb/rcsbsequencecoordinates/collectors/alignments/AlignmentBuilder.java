/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.alignments;

import org.bson.Document;
import org.rcsb.graphqlschema.reference.SequenceReference;
import reactor.core.publisher.Flux;

/**
 * @author : joan
*/
public interface AlignmentBuilder {
    Flux<Document> apply(String queryId, SequenceReference from, SequenceReference to);
}
