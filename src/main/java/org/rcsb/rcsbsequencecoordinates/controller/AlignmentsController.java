package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.rcsb.collectors.SequenceCollector;
import org.rcsb.graphqlschema.query.AlignmentsQuery;
import org.rcsb.graphqlschema.schema.SchemaFieldConstants;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.response.SequenceAlignments;
import org.rcsb.graphqlschema.response.TargetAlignment;
import org.rcsb.rcsbsequencecoordinates.configuration.GraphqlSchemaMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.rcsb.collectors.TargetAlignmentsCollector.getAlignments;
import static org.rcsb.utils.GraphqlMethods.getArgument;
import static org.rcsb.utils.GraphqlMethods.getQueryName;


@Controller
public class AlignmentsController implements AlignmentsQuery<Mono<SequenceAlignments>> {

    private static final Logger logger = LoggerFactory.getLogger(AlignmentsController.class);
    @QueryMapping(name = SchemaFieldConstants.ALIGNMENTS)
    public Mono<SequenceAlignments> alignments(
            @Argument(name = SchemaFieldConstants.QUERY_ID) String queryId,
            @Argument(name = SchemaFieldConstants.FROM) SequenceReference from,
            @Argument(name = SchemaFieldConstants.TO) SequenceReference to
    ) {
        return Mono.just(new SequenceAlignments());
    }

    @QueryMapping(name = SchemaFieldConstants.GROUP_ALIGNMENTS)
    public Mono<SequenceAlignments> group_alignments(
            @Argument(name = SchemaFieldConstants.GROUP_ID) String groupId,
            @Argument(name = SchemaFieldConstants.GROUP) GroupReference group
    ) {
        return Mono.just(new SequenceAlignments());
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.TARGET_ALIGNMENTS)
    public Flux<TargetAlignment> getTargetAlignments(DataFetchingEnvironment dataFetchingEnvironment){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaFieldConstants.ALIGNMENTS))
            return getAlignments(
                getArgument(dataFetchingEnvironment, SchemaFieldConstants.QUERY_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaFieldConstants.FROM)),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaFieldConstants.TO))
            );
        if(getQueryName(dataFetchingEnvironment).equals(SchemaFieldConstants.GROUP_ALIGNMENTS))
            return getAlignments(
                getArgument(dataFetchingEnvironment, SchemaFieldConstants.GROUP_ID),
                GroupReference.valueOf(getArgument(dataFetchingEnvironment, SchemaFieldConstants.GROUP))
            ) ;
        throw new RuntimeException(String.format("Undefined end point query %s", getQueryName(dataFetchingEnvironment)));
    }

    @SchemaMapping(typeName = "TargetAlignment", field = GraphqlSchemaMapping.TARGET_SEQUENCE)
    public Mono<String> getTargetSequence(TargetAlignment targetAlignment){
        return SequenceCollector.getSequence( targetAlignment.getTargetId() );
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment){
        return SequenceCollector.getSequence( getArgument(dataFetchingEnvironment, SchemaFieldConstants.QUERY_ID) );
    }

    /*@SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }*/

}
