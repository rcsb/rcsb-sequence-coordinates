package org.rcsb.utils;

import graphql.schema.DataFetchingEnvironment;

public class GraphqlMethods {

    public static <T> T getArgument(DataFetchingEnvironment dataFetchingEnvironment, String argumentName) {
        return dataFetchingEnvironment.getExecutionStepInfo().getParent().getArgument(argumentName);
    }


    public static String getQueryName(DataFetchingEnvironment dataFetchingEnvironment){
        return dataFetchingEnvironment.getExecutionStepInfo().getParent().getPath().getSegmentName();
    }

}
