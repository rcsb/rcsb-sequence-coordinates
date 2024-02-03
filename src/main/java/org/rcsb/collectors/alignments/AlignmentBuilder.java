package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.graphqlschema.reference.SequenceReference;
import reactor.core.publisher.Flux;

public interface AlignmentBuilder {
    Flux<Document> apply(String queryId, SequenceReference from, SequenceReference to);
}
