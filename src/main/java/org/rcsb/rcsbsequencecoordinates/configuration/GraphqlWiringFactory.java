/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.configuration;

import graphql.schema.DataFetcher;
import graphql.schema.idl.FieldWiringEnvironment;
import graphql.schema.idl.WiringFactory;
import org.bson.Document;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

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