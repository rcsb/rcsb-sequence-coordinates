package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.rcsb.collectors.SequenceCollector;
import org.rcsb.graphqlschema.query.AlignmentsQuery;
import org.rcsb.graphqlschema.schema.SchemaConstants;
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
    @QueryMapping(name = SchemaConstants.Query.ALIGNMENTS)
    public Mono<SequenceAlignments> alignments(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.FROM) SequenceReference from,
            @Argument(name = SchemaConstants.Param.TO) SequenceReference to
    ) {
        return Mono.just(new SequenceAlignments());
    }

    @QueryMapping(name = SchemaConstants.Query.GROUP_ALIGNMENTS)
    public Mono<SequenceAlignments> group_alignments(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group
    ) {
        return Mono.just(new SequenceAlignments());
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.TARGET_ALIGNMENTS)
    public Flux<TargetAlignment> getTargetAlignments(DataFetchingEnvironment dataFetchingEnvironment){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.ALIGNMENTS))
            return getAlignments(
                getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM)),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
            );
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENTS))
            return getAlignments(
                getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_ID),
                GroupReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP))
            ) ;
        throw new RuntimeException(String.format("Undefined end point query %s", getQueryName(dataFetchingEnvironment)));
    }

    @SchemaMapping(typeName = "TargetAlignment", field = GraphqlSchemaMapping.TARGET_SEQUENCE)
    public Mono<String> getTargetSequence(TargetAlignment targetAlignment){
        return SequenceCollector.getSequence( targetAlignment.getTargetId() );
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment){
        return SequenceCollector.getSequence( getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID) );
    }

    /*@SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }*/

}
