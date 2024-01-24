package org.rcsb.rcsbsequencecoordinates.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.schema.DataFetcher;
import graphql.schema.PropertyDataFetcher;
import graphql.schema.idl.FieldWiringEnvironment;
import graphql.schema.idl.WiringFactory;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class GraphqlWiringFactory implements WiringFactory {

    @Override
    public boolean providesDataFetcher(FieldWiringEnvironment fieldWiringEnvironment) {
        return !(
                fieldWiringEnvironment.getParentType().getName().equals("Query") ||
                fieldWiringEnvironment.getParentType().getName().equals("Subscription") ||
                GraphqlSchemaMapping.getFields().contains(fieldWiringEnvironment.getFieldDefinition().getName())
        );
    }
    @Override
    public DataFetcher<?> getDataFetcher(FieldWiringEnvironment fieldWiringEnvironment) {
        return dataFetcher -> ((Document)dataFetcher.getSource()).get(dataFetcher.getField().getName());
    }

}