/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.map;

import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.CoreConstants;
import org.rcsb.utils.IdentifierSeparator;
import org.rcsb.utils.MongoStream;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;
import static org.rcsb.collectors.map.MapHelper.*;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class MapCollector {

    public static Flux<String> getQueryIdMap(String queryId, SequenceReference reference){
        if (Objects.requireNonNull(reference) == SequenceReference.PDB_INSTANCE)
            return pdbInstanceToEntityMap(queryId);
        return Flux.just(queryId);
    }

    public static Flux<String> getTargetIdMap(String targetId, SequenceReference reference){
        if (Objects.requireNonNull(reference) == SequenceReference.PDB_INSTANCE)
            return pdbEntityToInstanceMap(targetId);
        return Flux.just(targetId);
    }

    public static Flux<String> mapIds(SequenceReference from, SequenceReference to, List<String> ids){
        if(from.equals(to))
            return Flux.fromIterable(ids);
        return translateInstancesToEntities(ids);
    }

    private static Flux<String> translateInstancesToEntities(List<String> instanceIds) {
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getPdbInstanceMapCollection()).aggregate(
                List.of(match(or(instanceIds.stream().map(
                        instanceId -> and(
                                eq(CoreConstants.ENTRY_ID, parseEntryFromInstance(instanceId)),
                                eq(CoreConstants.ASYM_ID, parseAsymFromInstance(instanceId))
                        )
                ).toList())))
        )).map(
            document -> String.format(
                    "%s%s%s",
                    document.getString(CoreConstants.ENTRY_ID),
                    IdentifierSeparator.ENTITY_SEPARATOR,
                    document.getString(CoreConstants.ENTITY_ID)
            )
        );
    }

    private static Flux<String> pdbInstanceToEntityMap(String id){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getPdbInstanceMapCollection()).aggregate(List.of(
                match(eq(getEntryIdField(), parseEntryFromInstance(id))),
                match(eq(getAsymIdField(), parseAsymFromInstance(id))),
                pdbInstanceMapFields()
        ))).map(MapHelper::entityFromInstanceMap);
    }

    private static Flux<String> pdbEntityToInstanceMap(String id){
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getPdbInstanceMapCollection()).aggregate(List.of(
                match(eq(getEntryIdField(), parseEntryFromEntity(id))),
                match(eq(getEntityIdField(), parseEntityFromEntity(id))),
                pdbInstanceMapFields()
        ))).map(MapHelper::instanceFromInstanceMap);
    }

}
