package org.rcsb.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CustomObjectMapper {

    private static ObjectMapper serializationMapper;
    private static ObjectMapper deserializationMapper;

    /**
     * Get the singleton mapper to be used for serializing (object to json).
     * @return the mapper
     */
    public static ObjectMapper getSerializationMapper() {
        if (serializationMapper == null) {
            serializationMapper = new ObjectMapper();

            // for serialization we want nulls and empty values (empty strings, empty arrays) not to appear in json
            serializationMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            // for serialization we want the POJOs camel case to be converted to snake case
            serializationMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

            // for the moment we are ignoring the annotations. Revisit if we decide we need them
            // NOTE: there's some weird interactions with the inclusion strategy when this is set to true, that's one of the reasons we don't want annotations
            serializationMapper.configure(MapperFeature.USE_ANNOTATIONS, false);

            // for serialization of Date fields we want to be consistent with GraphQL Date that is RFC 3339 compliant.
            // NOTE: the ObjectMapper instance used by the web layer isn't shared with SPQR used to build GraphQL schema.
            DateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            serializationMapper.setDateFormat(rfc3339);
        }

        return serializationMapper;
    }

    /**
     * Get the singleton mapper to be used for deserializing (from json to object).
     * @return the mapper
     */
    public static ObjectMapper getDeserializationMapper() {
        if (deserializationMapper == null) {
            deserializationMapper = new ObjectMapper();
            // we need the annotations for deserialization because the objects in mongo follow the annotations
            // TODO if we drop annotations we can drop this
            deserializationMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        }

        return deserializationMapper;
    }
}


