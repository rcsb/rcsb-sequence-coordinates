/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.conversions.Bson;
import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.springframework.data.mongodb.core.index.IndexDirection;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.rcsb.collectors.alignments.AlignmentsReferenceHelper.*;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/26/24, Monday
 **/
public class AlignmentsMongoHelper {

    public static String getCollection(SequenceReference from, SequenceReference to) {
        if(testGenome(from,to))
            return MongoCollections.COLL_SEQUENCE_COORDINATES_NCBI_GENOME_TO_PROTEIN_ALIGNMENTS;
        if(testUniprot(from, to) && testPdb(from, to))
            return MongoCollections.COLL_SEQUENCE_COORDINATES_UNIPROT_TO_PDB_ENTITY_ALIGNMENTS;
        if(testNcbi(from, to) && testPdb(from, to))
            return MongoCollections.COLL_SEQUENCE_COORDINATES_NCBI_TO_PDB_ENTITY_ALIGNMENTS;
        if(testNcbi(from, to) && testUniprot(from, to))
            return MongoCollections.COLL_SEQUENCE_COORDINATES_NCBI_TO_UNIPROT_ALIGNMENTS;
        throw new RuntimeException(
                String.format(
                        "Unknown mapping references from %s to %s",
                        from,
                        to
                )
        );
    }

    public static String getIndex(SequenceReference from, SequenceReference to){
        if(testGenome(from))
            return SequenceCoordinatesConstants.QUERY_ID;
        if(testGenome(to))
            return SequenceCoordinatesConstants.TARGET_ID;
        if(testNcbi(from))
            return SequenceCoordinatesConstants.QUERY_ID;
        if(testNcbi(to))
            return SequenceCoordinatesConstants.TARGET_ID;
        if(testUniprot(from))
            return SequenceCoordinatesConstants.QUERY_ID;
        if(testUniprot(to))
            return SequenceCoordinatesConstants.TARGET_ID;
        if(testPdb(from) && testPdb(to))
            return SequenceCoordinatesConstants.QUERY_ID;
        throw new RuntimeException(
                String.format(
                        "Unknown index for references from %s to %s",
                        from,
                        to
                )
        );
    }

    public static String getAltIndex(SequenceReference from, SequenceReference to) {
        return getAltIndex(getIndex(from,to));
    }

    public static String getAltIndex(String attribute) {
        if(attribute.equals(SequenceCoordinatesConstants.QUERY_ID))
            return SequenceCoordinatesConstants.TARGET_ID;
        return SequenceCoordinatesConstants.QUERY_ID;
    }

    public static String getLengthAttribute(SequenceReference from, SequenceReference to){
        String index = getIndex(from, to);
        if(index.equals(SequenceCoordinatesConstants.QUERY_ID))
            return SequenceCoordinatesConstants.QUERY_LENGTH;
        return SequenceCoordinatesConstants.TARGET_LENGTH;
    }


    public static Bson getSortFields(SequenceReference from, SequenceReference to){
        if(from.equals(SequenceReference.NCBI_PROTEIN))
            return sortAggregator(SequenceCoordinatesConstants.QUERY_BEGIN, SequenceCoordinatesConstants.QUERY_END);
        if(to.equals(SequenceReference.NCBI_PROTEIN))
            return sortAggregator(SequenceCoordinatesConstants.TARGET_BEGIN, SequenceCoordinatesConstants.TARGET_END);
        if(from.equals(SequenceReference.UNIPROT))
            return sortAggregator(SequenceCoordinatesConstants.QUERY_BEGIN, SequenceCoordinatesConstants.QUERY_END);
        if(to.equals(SequenceReference.UNIPROT))
            return sortAggregator(SequenceCoordinatesConstants.TARGET_BEGIN, SequenceCoordinatesConstants.TARGET_END);
        if(equivalentReferences(from, to))
            return sortAggregator(SequenceCoordinatesConstants.QUERY_BEGIN, SequenceCoordinatesConstants.QUERY_END);
        throw new RuntimeException(
                String.format(
                        "Unknown index for references from %s to %s",
                        from,
                        to
                )
        );
    }

    public static String getGroupCollection(){
        return MongoCollections.COLL_SEQUENCE_COORDINATES_SEQUENCE_IDENTITY_GROUP_ALIGNMENTS;
    }

    public static String getGroupIndex(){
        return SequenceCoordinatesConstants.QUERY_ID;
    }

    public static String getGroupLengthAttribute(){
        return SequenceCoordinatesConstants.QUERY_LENGTH;
    }

    public static String getQueryIndex(){
        return SequenceCoordinatesConstants.QUERY_ID;
    }

    public static String getQueryBegin(){
        return SequenceCoordinatesConstants.QUERY_BEGIN;
    }

    public static String getQueryEnd(){
        return SequenceCoordinatesConstants.QUERY_END;
    }

    public static String getTargetIndex(){
        return SequenceCoordinatesConstants.TARGET_ID;
    }

    public static String getTargetCoverage() {
        return SequenceCoordinatesConstants.COVERAGE + "." + SequenceCoordinatesConstants.TARGET_COVERAGE;
    }

    public static Bson getGroupSortFields(){
        return sortAggregator(
                SequenceCoordinatesConstants.QUERY_BEGIN,
                SequenceCoordinatesConstants.QUERY_END,
                SequenceCoordinatesConstants.TARGET_ID
        );
    }

    public static Bson alignmentFields() {
        return project(fields(
                include(SequenceCoordinatesConstants.TARGET_ID),
                include(SequenceCoordinatesConstants.QUERY_ID),
                include(SequenceCoordinatesConstants.ALIGNED_REGIONS),
                include(SequenceCoordinatesConstants.COVERAGE),
                excludeId()
        ));
    }

    public static Bson genomeFields() {
        return project(fields(
                include(SequenceCoordinatesConstants.TARGET_ID),
                include(SequenceCoordinatesConstants.QUERY_ID),
                include(SequenceCoordinatesConstants.ALIGNED_REGIONS),
                include(SequenceCoordinatesConstants.QUERY_BEGIN),
                include(SequenceCoordinatesConstants.QUERY_END),
                include(SequenceCoordinatesConstants.ORIENTATION),
                include(SequenceCoordinatesConstants.COVERAGE),
                excludeId()
        ));
    }

    public static Bson mapFields() {
        return project(fields(
                include(SequenceCoordinatesConstants.TARGET_ID),
                include(SequenceCoordinatesConstants.QUERY_ID),
                excludeId()
        ));
    }

    public static Bson alignmentLengthFields() {
        return project(fields(
                include(SequenceCoordinatesConstants.COVERAGE),
                excludeId()
        ));
    }

    public static Bson sortAggregator(String fieldA, String fieldB) {
        return sort(orderBy(
                ascending(fieldA),
                descending(fieldB)
        ));
    }

    public static Bson sortAggregator(String fieldA, String fieldB, String fieldC) {
        return sort(orderBy(
                ascending(fieldA),
                descending(fieldB),
                ascending(fieldC)
        ));
    }

    public static Bson sortAggregator(String fieldA, IndexDirection direction) {
        switch (direction){
            case DESCENDING -> {
                return sort(orderBy(
                        descending(fieldA)
                ));
            }
            case ASCENDING -> {
                return sort(orderBy(
                        ascending(fieldA)
                ));
            }
            default -> throw new RuntimeException(String.format("Unknown index direction %s", direction));
        }
    }

}
