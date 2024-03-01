/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.springframework.data.mongodb.core.index.IndexDirection;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/23/24, Friday
 **/
public interface AlignmentsCollector {

    AlignmentsCollector range(List<Integer> range);
    AlignmentsCollector filter(List<String> filter);
    AlignmentsCollector page(Integer first, Integer offset);
    AlignmentsCollector sort(String field, IndexDirection direction);
    AlignmentsCollector max(int n);
    Flux<Document> get();

}
