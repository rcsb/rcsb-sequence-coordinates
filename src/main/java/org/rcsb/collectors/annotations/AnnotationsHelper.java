/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.annotations;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.common.constants.MongoCollections;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.CoreConstants;
import org.rcsb.mojave.auto.Feature;
import org.rcsb.utils.Range;

import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static org.rcsb.collectors.map.MapHelper.parseAsymFromInstance;
import static org.rcsb.collectors.map.MapHelper.parseEntryFromInstance;
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
                    match(eq(CoreConstants.TARGET_IDENTIFIERS+"."+CoreConstants.ENTRY_ID, parseEntryFromInstance(targetId))),
                    match(eq(CoreConstants.TARGET_IDENTIFIERS+"."+CoreConstants.ASYM_ID, parseAsymFromInstance(targetId))),
                    annotationsFields()
            );
        return List.of(
                match(eq(CoreConstants.TARGET_ID, targetId)),
                annotationsFields()
        );
    }

    public static Document mapAnnotations(Document annotations, Document alignment){
        annotations.put(
                SchemaConstants.Field.FEATURES,
                annotations.getList(CoreConstants.FEATURES, Document.class).stream()
                        .map(feature-> mapFeature(feature, alignment))
                        .filter(d->!d.getList(CoreConstants.FEATURE_POSITIONS, Document.class).isEmpty())
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

    private static Bson annotationsFields() {
        return project(fields(
                include(CoreConstants.TARGET_ID),
                include(CoreConstants.FEATURES),
                include(CoreConstants.TARGET_IDENTIFIERS),
                excludeId()
        ));
    }

    private static Document mapFeature(Document feature, Document alignment){
        feature.put(
                SchemaConstants.Field.FEATURE_POSITIONS,
                feature.getList(CoreConstants.FEATURE_POSITIONS, Document.class).stream().flatMap(
                        featurePosition-> featurePositionIntersection(featurePosition, alignment).stream()
                ).toList()
        );
        feature.put(
                SchemaConstants.Field.TYPE,
                Feature.Type.fromValue(feature.getString(CoreConstants.TYPE)).name()
        );
        return feature;
    }

    private static List<Document> featurePositionIntersection(Document featurePosition, Document alignment){
        return alignment.getList(CoreConstants.ALIGNED_REGIONS, Document.class).stream().map(
                alignmentRegion -> featureToAlignmentIntersection(featurePosition,alignmentRegion)
        ).filter(d->!d.isEmpty()).toList();
    }

    private static Document featureToAlignmentIntersection(Document featurePosition, Document alignmentRegion){
        if(featurePosition.containsKey(CoreConstants.VALUES))
            return valuesIntersection(featurePosition, alignmentRegion);
        return regionIntersection(featurePosition, alignmentRegion);

    }

    private static Document valuesIntersection(Document featureRegion, Document alignmentRegion){
        Range targetRange = new Range(
                alignmentRegion.getInteger(CoreConstants.TARGET_BEGIN),
                alignmentRegion.getInteger(CoreConstants.TARGET_END)
        );
        List<Double> featureValues = featureRegion.getList(CoreConstants.VALUES, Double.class);
        Range featureRange = new Range(
                featureRegion.getInteger(CoreConstants.BEG_SEQ_ID),
                featureRegion.getInteger(CoreConstants.BEG_SEQ_ID) + featureValues.size() - 1
        );
        Range intersection = intersection(targetRange, featureRange);
        if(intersection.isEmpty())
            return new Document();
        Range queryRange = new Range(
                alignmentRegion.getInteger(CoreConstants.QUERY_BEGIN),
                alignmentRegion.getInteger(CoreConstants.QUERY_END)
        );
        return new Document(Map.of(
                SchemaConstants.Field.BEG_SEQ_ID, mapIndex(intersection.bottom(), targetRange, queryRange),
                SchemaConstants.Field.BEG_ORI_ID, intersection.bottom(),
                SchemaConstants.Field.VALUES, featureValues.subList(
                        intersection.bottom() - featureRange.bottom(),
                        intersection.bottom() - featureRange.bottom() + intersection.size()
                )
        ));
    }

    private static Document regionIntersection(Document featureRegion, Document alignmentRegion){
        Range targetRange = new Range(
                alignmentRegion.getInteger(CoreConstants.TARGET_BEGIN),
                alignmentRegion.getInteger(CoreConstants.TARGET_END)
        );
        if(!featureRegion.containsKey(CoreConstants.END_SEQ_ID))
            featureRegion.put(CoreConstants.END_SEQ_ID, featureRegion.getInteger(CoreConstants.BEG_SEQ_ID));
        Range featureRange = new Range(
                featureRegion.getInteger(CoreConstants.BEG_SEQ_ID),
                featureRegion.getInteger(CoreConstants.END_SEQ_ID)
        );
        Range intersection = intersection(targetRange, featureRange);
        if(intersection.isEmpty())
            return new Document();

        Range queryRange = new Range(
                alignmentRegion.getInteger(CoreConstants.QUERY_BEGIN),
                alignmentRegion.getInteger(CoreConstants.QUERY_END)
        );
        return new Document(Map.of(
                SchemaConstants.Field.BEG_SEQ_ID, mapIndex(intersection.bottom(), targetRange, queryRange),
                SchemaConstants.Field.END_SEQ_ID, mapIndex(intersection.top(), targetRange, queryRange),
                SchemaConstants.Field.BEG_ORI_ID, intersection.bottom(),
                SchemaConstants.Field.END_ORI_ID, intersection.top()
        ));
    }

}
