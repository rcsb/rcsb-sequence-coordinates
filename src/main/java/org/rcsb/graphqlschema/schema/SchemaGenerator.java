package org.rcsb.graphqlschema.schema;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.metadata.strategy.query.AbstractResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.BeanResolverBuilder;
import org.rcsb.graphqlschema.queries.ServiceQueries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public class SchemaGenerator {

    public static GraphQLSchema buildSchema() {

        // custom resolver builder to inject our own behaviour to beans within the specified packages
        AbstractResolverBuilder customBean = new BeanResolverBuilder("org.rcsb.rcsbsequencecoordinates.auto")
                //This solves the error of `PageInfo` being redefined. However, pagination related fields are dispatched as camelCase
                //Error: You have redefined the type 'PageInfo' from being a 'GraphQLObjectType' to a 'GraphQLObjectType'
                //For some reason `CustomOperationNameGenerator` redefines PageInfo type
                .withFilters(member -> member.getDeclaringClass().getName().contains("org.rcsb.rcsbsequencecoordinates.auto"))
                .withOperationInfoGenerator(new CustomOperationNameGenerator());

        ServiceQueries serviceQueries = new ServiceQueries();
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withOperationsFromSingleton(serviceQueries)
                .withNestedResolverBuilders(customBean)
                // the ObjectMapper instance used by the web layer isn't shared with SPQR. You can reuse
                // the settings by providing the prototype instance to JacksonValueMapperFactory.
                //.withValueMapperFactory(new JacksonValueMapperFactory())
                .generate();

        return schema;
    }

    private static void saveSchema(GraphQLSchema schema, String fileName) {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(new SchemaPrinter().print(schema));
        }catch (FileNotFoundException e){
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(args[0]);
        saveSchema(
                buildSchema(),
                args[0]
        );
    }

}
