/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.rcsb.common.constants.MongoCollections;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.utils.IdentifierSeparator;

import java.util.Arrays;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.*;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class MapHelper {

    public static String getPdbInstanceMapCollection(){
        return MongoCollections.COLL_SEQUENCE_COORDINATES_PDB_INSTANCE_MAP;
    }

    public static Bson pdbInstanceMapFields() {
        return project(fields(
                include(SequenceCoordinatesConstants.ENTRY_ID),
                include(SequenceCoordinatesConstants.ENTITY_ID),
                include(SequenceCoordinatesConstants.ASYM_ID),
                excludeId()
        ));
    }

    public static String getEntryIdField(){
        return SequenceCoordinatesConstants.ENTRY_ID;
    }

    public static String getEntityIdField(){
        return SequenceCoordinatesConstants.ENTITY_ID;
    }

    public static String getAsymIdField(){
        return SequenceCoordinatesConstants.ASYM_ID;
    }

    public static String parseEntryFromInstance(String queryId){
        assert queryId.contains(IdentifierSeparator.ENTITY_INSTANCE_SEPARATOR);
        return queryId.split("\\"+IdentifierSeparator.ENTITY_INSTANCE_SEPARATOR)[0];
    }

    public static String parseAsymFromInstance(String queryId){
        assert queryId.contains(IdentifierSeparator.ENTITY_INSTANCE_SEPARATOR);
        return queryId.split("\\"+IdentifierSeparator.ENTITY_INSTANCE_SEPARATOR)[1];
    }

    public static String parseEntryFromEntity(String queryId){
        assert queryId.contains(IdentifierSeparator.ENTITY_SEPARATOR);
        String[] queryArray = queryId.split(IdentifierSeparator.ENTITY_SEPARATOR);
        return String.join(
                IdentifierSeparator.ENTITY_SEPARATOR,
                Arrays.copyOfRange(queryArray, 0, queryArray.length -1)
        );
    }

    public static String parseEntityFromEntity(String queryId){
        assert queryId.contains(IdentifierSeparator.ENTITY_SEPARATOR);
        String[] queryArray = queryId.split(IdentifierSeparator.ENTITY_SEPARATOR);
        return queryArray[queryArray.length -1];
    }

    public static String entityFromInstanceMap(Document map){
        return String.join(
                IdentifierSeparator.ENTITY_SEPARATOR,
                map.getString(SequenceCoordinatesConstants.ENTRY_ID),
                map.getString(SequenceCoordinatesConstants.ENTITY_ID)
        );
    }

    public static String instanceFromInstanceMap(Document map){
        return String.join(
                IdentifierSeparator.ENTITY_INSTANCE_SEPARATOR,
                map.getString(SequenceCoordinatesConstants.ENTRY_ID),
                map.getString(SequenceCoordinatesConstants.ASYM_ID)
        );
    }

}
