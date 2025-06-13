/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.annotations;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.mojave.auto.FeaturesType;
import org.rcsb.utils.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.map.MapHelper.parseAsymFromInstance;
import static org.rcsb.rcsbsequencecoordinates.collectors.map.MapHelper.parseEntryFromInstance;
import static org.rcsb.utils.RangeMethods.intersection;
import static org.rcsb.utils.RangeMethods.mapIndex;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class AnnotationsHelper {

    public static String getCollection(AnnotationReference annotationReference) {
        return switch (annotationReference) {
            case PDB_ENTITY -> MongoCollections.COLL_SEQUENCE_COORDINATES_PDB_ENTITY_ANNOTATIONS;
            case UNIPROT -> MongoCollections.COLL_SEQUENCE_COORDINATES_UNIPROT_ANNOTATIONS;
            case PDB_INSTANCE -> MongoCollections.COLL_SEQUENCE_COORDINATES_PDB_INSTANCE_ANNOTATIONS;
            case PDB_INTERFACE -> MongoCollections.COLL_SEQUENCE_COORDINATES_PDB_INTERFACE_ANNOTATIONS;
        };
    }

    public static List<Bson> getAggregation(String targetId, AnnotationReference annotationReference){
        if(annotationReference.equals(AnnotationReference.PDB_INTERFACE))
            return List.of(
                    match(eq(SequenceCoordinatesConstants.TARGET_IDENTIFIERS+"."+SequenceCoordinatesConstants.ENTRY_ID, parseEntryFromInstance(targetId))),
                    match(eq(SequenceCoordinatesConstants.TARGET_IDENTIFIERS+"."+SequenceCoordinatesConstants.ASYM_ID, parseAsymFromInstance(targetId))),
                    annotationsFields()
            );
        return List.of(
                match(eq(SequenceCoordinatesConstants.TARGET_ID, targetId)),
                annotationsFields()
        );
    }

    public static List<Bson> getAggregation(List<String> ids){
        List<Bson> aggregation = new ArrayList<>(ids.stream().map(
                id -> eq(SequenceCoordinatesConstants.TARGET_ID, id)
        ).toList());
        return List.of(
                match(or(aggregation)),
                mapFields()
        );
    }

    public static Document mapAnnotations(Document annotations, Document alignment){
        annotations.put(
                SchemaConstants.Field.FEATURES,
                annotations.getList(SequenceCoordinatesConstants.FEATURES, Document.class).stream()
                        .map(feature-> mapFeature(feature, alignment))
                        .filter(d->!d.getList(SequenceCoordinatesConstants.FEATURE_POSITIONS, Document.class).isEmpty())
                        .toList()
        );
        return annotations;
    }

    public static Document addSource(AnnotationReference annotationReference, Document annotations){
        annotations.put(
                SchemaConstants.Field.SOURCE,
                annotationReference
        );
        return annotations;
    }

    public static boolean hasFeatures(Document annotations){
        return annotations.containsKey(SchemaConstants.Field.FEATURES) && !annotations.getList(SchemaConstants.Field.FEATURES, Document.class).isEmpty();
    }

    public static String getTargetIdentifiersAttribute(){
        return SequenceCoordinatesConstants.TARGET_IDENTIFIERS;
    }

    private static Bson annotationsFields() {
        return project(fields(
                include(SequenceCoordinatesConstants.TARGET_ID),
                include(SequenceCoordinatesConstants.FEATURES),
                include(SequenceCoordinatesConstants.TARGET_IDENTIFIERS),
                excludeId()
        ));
    }

    private static Bson mapFields() {
        return project(fields(
                include(SequenceCoordinatesConstants.TARGET_ID),
                include(SequenceCoordinatesConstants.TARGET_IDENTIFIERS),
                excludeId()
        ));
    }

    private static Document mapFeature(Document feature, Document alignment){
        AtomicInteger featurePositionId = new AtomicInteger(1);
        feature.put(
                SchemaConstants.Field.FEATURE_POSITIONS,
                feature.getList(SequenceCoordinatesConstants.FEATURE_POSITIONS, Document.class).stream()
                        .flatMap(featurePosition -> featurePositionIntersection(
                                featurePosition,
                                alignment,
                                featurePositionId.getAndIncrement()
                        ).stream())
                        .toList()
        );
        feature.put(
                SchemaConstants.Field.TYPE,
                FeaturesType.fromValue(feature.getString(SequenceCoordinatesConstants.TYPE)).name()
        );
        return feature;
    }

    private static List<Document> featurePositionIntersection(Document featurePosition, Document alignment, int rangeId){
        return alignment.getList(SequenceCoordinatesConstants.ALIGNED_REGIONS, Document.class).stream()
                .map(alignmentRegion -> featureToAlignmentIntersection(featurePosition,alignmentRegion))
                .filter(d->!d.isEmpty())
                .peek(alignmentRegion -> alignmentRegion.put( SchemaConstants.Field.RANGE_ID, String.format("range-%s", rangeId)))
                .toList();
    }

    private static Document featureToAlignmentIntersection(Document featurePosition, Document alignmentRegion){
        if(featurePosition.containsKey(SequenceCoordinatesConstants.VALUES))
            return valuesIntersection(featurePosition, alignmentRegion);
        return regionIntersection(featurePosition, alignmentRegion);

    }

    private static Document valuesIntersection(Document featureRegion, Document alignmentRegion){
        Range targetRange = new Range(
                alignmentRegion.getInteger(SequenceCoordinatesConstants.TARGET_BEGIN),
                alignmentRegion.getInteger(SequenceCoordinatesConstants.TARGET_END)
        );
        List<Double> featureValues = featureRegion.getList(SequenceCoordinatesConstants.VALUES, Double.class);
        Range featureRange = new Range(
                featureRegion.getInteger(SequenceCoordinatesConstants.BEG_SEQ_ID),
                featureRegion.getInteger(SequenceCoordinatesConstants.BEG_SEQ_ID) + featureValues.size() - 1
        );
        Range intersection = intersection(targetRange, featureRange);
        if(intersection.isEmpty())
            return new Document();
        Range queryRange = new Range(
                alignmentRegion.getInteger(SequenceCoordinatesConstants.QUERY_BEGIN),
                alignmentRegion.getInteger(SequenceCoordinatesConstants.QUERY_END)
        );
        return new Document(Map.of(
                SchemaConstants.Field.BEG_SEQ_ID, mapIndex(intersection.bottom(), targetRange, queryRange),
                SchemaConstants.Field.BEG_ORI_ID, intersection.bottom(),
                SchemaConstants.Field.VALUES, featureValues.subList(
                        intersection.bottom() - featureRange.bottom(),
                        intersection.bottom() - featureRange.bottom() + intersection.size()
                ),
                SchemaConstants.Field.OPEN_BEGIN, intersection.bottom() != featureRegion.getInteger(SequenceCoordinatesConstants.BEG_SEQ_ID),
                SchemaConstants.Field.OPEN_END, intersection.top() != featureRegion.getInteger(SequenceCoordinatesConstants.BEG_SEQ_ID) + featureValues.size() - 1
        ));
    }

    private static Document regionIntersection(Document featureRegion, Document alignmentRegion){
        Range targetRange = new Range(
                alignmentRegion.getInteger(SequenceCoordinatesConstants.TARGET_BEGIN),
                alignmentRegion.getInteger(SequenceCoordinatesConstants.TARGET_END)
        );
        if(!featureRegion.containsKey(SequenceCoordinatesConstants.END_SEQ_ID))
            featureRegion.put(SequenceCoordinatesConstants.END_SEQ_ID, featureRegion.getInteger(SequenceCoordinatesConstants.BEG_SEQ_ID));
        Range featureRange = new Range(
                featureRegion.getInteger(SequenceCoordinatesConstants.BEG_SEQ_ID),
                featureRegion.getInteger(SequenceCoordinatesConstants.END_SEQ_ID)
        );
        Range intersection = intersection(targetRange, featureRange);
        if(intersection.isEmpty())
            return new Document();

        Range queryRange = new Range(
                alignmentRegion.getInteger(SequenceCoordinatesConstants.QUERY_BEGIN),
                alignmentRegion.getInteger(SequenceCoordinatesConstants.QUERY_END)
        );
        return new Document(Map.of(
                SchemaConstants.Field.BEG_SEQ_ID, mapIndex(intersection.bottom(), targetRange, queryRange),
                SchemaConstants.Field.END_SEQ_ID, mapIndex(intersection.top(), targetRange, queryRange),
                SchemaConstants.Field.BEG_ORI_ID, intersection.bottom(),
                SchemaConstants.Field.END_ORI_ID, intersection.top(),
                SchemaConstants.Field.OPEN_BEGIN, intersection.bottom() != featureRegion.getInteger(SequenceCoordinatesConstants.BEG_SEQ_ID),
                SchemaConstants.Field.OPEN_END, intersection.top() != featureRegion.getInteger(SequenceCoordinatesConstants.END_SEQ_ID)
        ));
    }

}
