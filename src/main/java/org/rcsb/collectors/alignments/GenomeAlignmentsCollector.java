/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.collectors.map.MapCollector;
import org.rcsb.collectors.utils.GenomeRangeIntersection;
import org.rcsb.collectors.utils.RangeIntersectionOperator;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.utils.MongoStream;
import org.rcsb.utils.Range;
import org.springframework.data.mongodb.core.index.IndexDirection;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;
import static org.rcsb.collectors.alignments.AlignmentsDocumentHelper.switchAlignment;
import static org.rcsb.collectors.alignments.AlignmentsMongoHelper.*;
import static org.rcsb.collectors.alignments.AlignmentsReferenceHelper.testGenome;
import static org.rcsb.collectors.alignments.GenomeAlignmentsHelper.mergeProteinToGenomeRegions;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/23/24, Friday
 **/
public class GenomeAlignmentsCollector implements AlignmentsCollector {

    private final Supplier<Flux<Document>> documentSupplier;
    private final SequenceReference reference;
    private Function<Flux<Document>,Flux<Document>> filterRange = Function.identity();
    private Range range = new Range(0, -1);

    private GenomeAlignmentsCollector(String queryId, SequenceReference from, SequenceReference to){
        this.reference = from;
        if(testGenome(to))
            documentSupplier = () -> getProteinToGenome(queryId, from);
        else if(testGenome(from))
            documentSupplier = () -> getGenomeToProtein(queryId, to);
        else
            throw new RuntimeException(String.format(
                    "Genome alignments wrong references from: %s to: %s",
                    from,
                    to
            ));
    }

    public static AlignmentsCollector request(String queryId, SequenceReference from, SequenceReference to) {
        return new GenomeAlignmentsCollector(queryId, from, to);
    }

    @Override
    public AlignmentsCollector range(List<Integer> range) {
        this.range = (range != null && range.size() == 2) ? new Range(range.get(0), range.get(1)) : new Range(0, -1);

        RangeIntersectionOperator alignmentRangeIntersection = new RangeIntersectionOperator(range, new GenomeRangeIntersection(reference));
        this.filterRange = documentFlux -> documentFlux
                .filter(alignmentRangeIntersection::isConnected)
                .map(alignmentRangeIntersection::applyRange);

        return this;
    }

    @Override
    public AlignmentsCollector filter(List<String> filter) {
        return this;
    }

    @Override
    public AlignmentsCollector page(Integer first, Integer offset) {
        return this;
    }

    @Override
    public AlignmentsCollector sort(String field, IndexDirection direction) {
        return this;
    }

    @Override
    public AlignmentsCollector max(int n) {
        return this;
    }

    @Override
    public Flux<Document> get() {
        return documentSupplier
                .get()
                .transform(filterRange);
    }

    private Flux<Document> getProteinToGenome(String queryId, SequenceReference from){
        return ProteinAlignmentsCollector
                .request(queryId, from, SequenceReference.NCBI_PROTEIN)
                .sort(getTargetCoverage(), IndexDirection.DESCENDING)
                .max(1)
                .get()
                .flatMap(this::getNcbiProteinToGenome);
    }

    private Flux<Document> getGenomeToProtein(String queryId, SequenceReference to){
        return mapNcbiGenomeToProtein(queryId)
                .flatMap(map -> mapNcbiProteinToReference(map, to))
                .flatMap(targetId-> MapCollector.getTargetIdMap(targetId, to))
                .distinct()
                .flatMap(
                        targetId -> getProteinToGenome(targetId, to).map(d -> switchAlignment(targetId, d))
                );
    }

    private Flux<Document> getNcbiProteinToGenome(Document proteinAlignment){
        return getAlignmentDocuments(
                getCollection(SequenceReference.NCBI_PROTEIN, SequenceReference.NCBI_GENOME),
                getIndex(SequenceReference.NCBI_PROTEIN, SequenceReference.NCBI_GENOME),
                proteinAlignment.getString(SchemaConstants.Field.TARGET_ID)
        ).map(
                genomeAlignment -> mergeProteinToGenomeRegions(proteinAlignment, genomeAlignment)
        );
    }

    private Flux<Document> mapNcbiGenomeToProtein(String queryId){
        return getMapDocuments(
                getCollection(SequenceReference.NCBI_GENOME, SequenceReference.NCBI_PROTEIN),
                getIndex(SequenceReference.NCBI_GENOME, SequenceReference.NCBI_PROTEIN),
                queryId
        );
    }

    private Flux<String> mapNcbiProteinToReference(Document genomeMap, SequenceReference to){
        return MapCollector.mapIds(SequenceReference.NCBI_PROTEIN, to, List.of(genomeMap.getString(SchemaConstants.Field.TARGET_ID)));
    }

    private Flux<Document> getAlignmentDocuments(String collection, String attribute, String id){
        List<Bson> aggregation = new ArrayList<>(List.of(
                match(eq(attribute, id)),
                genomeFields()
        ));
        return Flux.from(MongoStream.getMongoDatabase().getCollection(collection).aggregate(aggregation));
    }

    private Flux<Document> getMapDocuments(String collection, String attribute, String id){
        List<Bson> aggregation = new ArrayList<>(List.of(
                buildMatch(attribute, id),
                mapFields()
        ));
        return Flux.from(MongoStream.getMongoDatabase().getCollection(collection).aggregate(aggregation));
    }

    private Bson buildMatch(String attribute, String id){
        if(range.isEmpty())
            return match(eq(attribute, id));
        return match(and(
                eq(attribute, id),
                nor(or(
                        gt(getQueryBegin(),range.top()),
                        lt(getQueryEnd(),range.bottom())
                ))
        ));
    }

}
