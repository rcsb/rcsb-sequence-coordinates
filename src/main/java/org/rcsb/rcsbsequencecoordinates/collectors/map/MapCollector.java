/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.map;

import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.utils.MongoStream;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;
import static org.rcsb.rcsbsequencecoordinates.collectors.map.MapHelper.*;

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

    public static Flux<String> mapEquivalentReferences(SequenceReference from, SequenceReference to, List<String> ids){
        if(from.equals(SequenceReference.PDB_ENTITY) && to.equals(SequenceReference.PDB_INSTANCE))
            return pdbEntityToInstanceMap(ids);
        if(from.equals(SequenceReference.PDB_INSTANCE) && to.equals(SequenceReference.PDB_ENTITY))
            return pdbInstanceToEntityMap(ids);
        if(from.equals(to))
            return Flux.fromIterable(ids);
        throw new RuntimeException( String.format(
                "Reference map from: %s to: %s is not equivalent",
                from,
                to
        ));
    }

    private static Flux<String> pdbInstanceToEntityMap(String id){
        return pdbInstanceToEntityMap(List.of(id));
    }

    private static Flux<String> pdbInstanceToEntityMap(List<String> ids) {
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getPdbInstanceMapCollection()).aggregate(
                List.of(match(or(ids.stream().map(
                        id -> and(
                                eq(getEntryIdField(), parseEntryFromInstance(id)),
                                eq(getAsymIdField(), parseAsymFromInstance(id))
                        )
                ).toList())))
        )).map(MapHelper::entityFromInstanceMap);
    }

    private static Flux<String> pdbEntityToInstanceMap(String id){
        return pdbEntityToInstanceMap(List.of(id));
    }

    private static Flux<String> pdbEntityToInstanceMap(List<String> ids) {
        return Flux.from(MongoStream.getMongoDatabase().getCollection(getPdbInstanceMapCollection()).aggregate(
                List.of(match(or(ids.stream().map(
                        id -> and(
                                eq(getEntryIdField(), parseEntryFromEntity(id)),
                                eq(getEntityIdField(), parseEntityFromEntity(id))
                        )
                ).toList())))
        )).map(MapHelper::instanceFromInstanceMap);
    }

}
