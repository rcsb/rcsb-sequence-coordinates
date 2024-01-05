package org.rcsb.rcsbsequencecoordinates.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.schema.DataFetcher;
import graphql.schema.PropertyDataFetcher;
import graphql.schema.idl.FieldWiringEnvironment;
import graphql.schema.idl.WiringFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class GraphqlWiringFactory implements WiringFactory {

    @Override
    public boolean providesDataFetcher(FieldWiringEnvironment fieldWiringEnvironment) {
        return !(fieldWiringEnvironment.getParentType().getName().equals("Query") || GraphqlSchemaMapping.getFields().contains(fieldWiringEnvironment.getFieldDefinition().getName()));
    }
    @Override
    public DataFetcher<?> getDataFetcher(FieldWiringEnvironment fieldWiringEnvironment) {
        return dataFetchingEnvironment -> Arrays.stream(dataFetchingEnvironment.getSource().getClass().getDeclaredMethods()).filter(
                method -> (method.getName().contains("get") && method.getAnnotation(JsonProperty.class).value().equals(dataFetchingEnvironment.getField().getName()))
        )
        .findAny()
        .map(
            method -> {
                try {
                    return method.invoke(dataFetchingEnvironment.getSource());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        ).orElseGet(
            () -> new PropertyDataFetcher<>(dataFetchingEnvironment.getField().getName()).get(dataFetchingEnvironment)
        );
    }

}