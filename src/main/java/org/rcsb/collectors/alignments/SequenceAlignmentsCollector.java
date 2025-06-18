/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.collectors.map.MapCollector;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.utils.MongoStream;
import org.springframework.data.mongodb.core.index.IndexDirection;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;
import static org.rcsb.collectors.alignments.AlignmentsMongoHelper.*;
import static org.rcsb.collectors.alignments.AlignmentsReferenceHelper.equivalentReferences;
import static org.rcsb.collectors.alignments.AlignmentsReferenceHelper.testGenome;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/23/24, Friday
 **/
public class SequenceAlignmentsCollector implements AlignmentsCollector {

    public static AlignmentsCollector request(String queryId, SequenceReference from, SequenceReference to) {
        if(testGenome(from, to))
            return GenomeAlignmentsCollector.request(queryId, from, to);
        return ProteinAlignmentsCollector.request(queryId, from, to);
    }

    public static AlignmentsCollector request(String groupId, GroupReference group) {
        return ProteinAlignmentsCollector.request(groupId, group);
    }

    @Override
    public AlignmentsCollector range(List<Integer> range) {
        return null;
    }

    @Override
    public AlignmentsCollector filter(List<String> filter) {
        return null;
    }

    @Override
    public AlignmentsCollector page(Integer first, Integer offset) {
        return null;
    }

    @Override
    public AlignmentsCollector sort(String field, IndexDirection direction) {
        return null;
    }

    @Override
    public AlignmentsCollector max(int n) {
        return null;
    }

    @Override
    public Flux<Document> get() {
        return null;
    }

    public static Flux<String> mapIds(SequenceReference from, SequenceReference to, List<String> ids){
        return MapCollector.mapIds(from, to, ids);
    }

    private static Flux<Document> getMapDocuments(String collection, String attribute, List<String> ids){
        List<Bson> aggregation = List.of(
                match(in(attribute, ids)),
                mapFields()
        );
        return Flux.from(MongoStream.getMongoDatabase().getCollection(collection).aggregate(aggregation));
    }

}
