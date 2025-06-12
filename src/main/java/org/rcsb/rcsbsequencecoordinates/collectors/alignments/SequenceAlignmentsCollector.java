/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.alignments;

import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.rcsbsequencecoordinates.collectors.map.MapCollector;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsMongoHelper.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsReferenceHelper.equivalentReferences;
import static org.rcsb.rcsbsequencecoordinates.collectors.alignments.AlignmentsReferenceHelper.testGenome;

/**
 * @author : joan
 */
@Service
public class SequenceAlignmentsCollector implements AlignmentsCollector {

    private final MongoClient mongoClient;
    private final MongoProperties mongoProperties;
    private final MapCollector mapCollector;

    public SequenceAlignmentsCollector(MongoClient mongoClient, MongoProperties mongoProperties) {
        this.mongoClient = mongoClient;
        this.mongoProperties = mongoProperties;
        this.mapCollector = new MapCollector(mongoClient, mongoProperties);
    }

    public AlignmentsCollector request(String queryId, SequenceReference from, SequenceReference to) {
        if(testGenome(from, to))
            return GenomeAlignmentsCollector.request(queryId, from, to, mongoClient, mongoProperties, this);
        return ProteinAlignmentsCollector.request(mongoClient, mongoProperties, queryId, from, to);
    }

    public AlignmentsCollector request(String groupId, GroupReference group) {
        return ProteinAlignmentsCollector.request(mongoClient, mongoProperties, groupId, group);
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

    public Flux<String> mapIds(SequenceReference from, SequenceReference to, List<String> ids){
        if(equivalentReferences(from,to))
            return mapCollector.mapEquivalentReferences(from, to, ids);
        return getMapDocuments(
                getCollection(from, to),
                getIndex(from, to),
                ids
        ).map(map -> map.getString(getAltIndex(from, to)));
    }

    private Flux<Document> getMapDocuments(String collection, String attribute, List<String> ids){
        List<Bson> aggregation = List.of(
                match(in(attribute, ids)),
                mapFields()
        );
        return Flux.from(mongoClient.getDatabase(mongoProperties.getDatabase()).getCollection(collection).aggregate(aggregation));
    }

}
