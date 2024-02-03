package org.rcsb.utils;

import graphql.execution.ExecutionStepInfo;
import graphql.schema.DataFetchingEnvironment;

public class GraphqlMethods {

    public static <T> T getArgument(DataFetchingEnvironment dataFetchingEnvironment, String argumentName) {
        return getExecutionRoot(dataFetchingEnvironment.getExecutionStepInfo()).getArgument(argumentName);
    }

    public static String getQueryName(DataFetchingEnvironment dataFetchingEnvironment){
        return getExecutionRoot(dataFetchingEnvironment.getExecutionStepInfo()).getPath().getSegmentName();
    }

    private static ExecutionStepInfo getExecutionRoot(ExecutionStepInfo executionStepInfo){
        if(executionStepInfo.getPath().getLevel() == 1)
            return executionStepInfo;
        return getExecutionRoot(executionStepInfo.getParent());
    }

}
