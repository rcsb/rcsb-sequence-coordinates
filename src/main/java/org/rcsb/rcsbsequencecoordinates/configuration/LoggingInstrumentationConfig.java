/*
 *  Copyright (c) 2025 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.configuration;

import graphql.ExecutionInput;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jspecify.annotations.NonNull;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 9/23/25, Tuesday
 **/

@Configuration
public class LoggingInstrumentationConfig {

    private static final Logger log = LoggerFactory.getLogger(LoggingInstrumentationConfig.class);

    @Bean
    public Instrumentation loggingInstrumentation() {
        return new SimplePerformantInstrumentation() {
            @Override
            public @NonNull ExecutionInput instrumentExecutionInput(
                    ExecutionInput executionInput,
                    InstrumentationExecutionParameters parameters,
                    InstrumentationState state
            ) {
                log.debug("GraphQL query:\n{}\nvariables: {}", executionInput.getQuery(), executionInput.getVariables());
                return executionInput;
            }
        };
    }

}
