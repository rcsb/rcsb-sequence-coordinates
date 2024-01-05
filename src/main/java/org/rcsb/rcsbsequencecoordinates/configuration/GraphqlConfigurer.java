package org.rcsb.rcsbsequencecoordinates.configuration;

import graphql.schema.*;
import graphql.scalars.object.ObjectScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration(proxyBeanMethods = false)
public class GraphqlConfigurer {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .wiringFactory(new GraphqlWiringFactory())
                .scalar(buildObjectScalar())
                .build();
    }

    private GraphQLScalarType buildObjectScalar()  {
       return GraphQLScalarType.newScalar()
               .name("ObjectScalar")
               .description("Built-in scalar for dynamic values")
               .coercing(ObjectScalar.INSTANCE.getCoercing())
               .build();
    }

}
