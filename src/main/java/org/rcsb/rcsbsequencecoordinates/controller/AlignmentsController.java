package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.rcsb.collectors.SequenceCollector;
import org.rcsb.collectors.TargetAlignmentsCollector;
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

import static org.rcsb.utils.GraphqlMethods.getArgument;


@Controller
public class AlignmentsController implements AlignmentsQuery<Mono<SequenceAlignments>> {

    private static final Logger logger = LoggerFactory.getLogger(AlignmentsController.class);
    @QueryMapping(name = SchemaFieldConstants.ALIGNMENTS)
    public Mono<SequenceAlignments> alignments(
            @Argument(name = SchemaFieldConstants.QUERY_ID) String queryId,
            @Argument(name = SchemaFieldConstants.FROM) SequenceReference from,
            @Argument(name = SchemaFieldConstants.TO) SequenceReference to
    ) {
        SequenceAlignments alignment = new SequenceAlignments();
        return Mono.just(alignment);
    }

    @QueryMapping(name = SchemaFieldConstants.GROUP_ALIGNMENTS)
    public Mono<SequenceAlignments> group_alignments(
            @Argument(name = SchemaFieldConstants.GROUP_ID) String groupId,
            @Argument(name = SchemaFieldConstants.GROUP) GroupReference group
    ) {
        SequenceAlignments alignment = new SequenceAlignments();
        return Mono.just(alignment);
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.TARGET_ALIGNMENTS)
    public Flux<TargetAlignment> getTargetAlignments(DataFetchingEnvironment dataFetchingEnvironment, SequenceAlignments sequenceAlignments){
        return TargetAlignmentsCollector.getAlignments(
            getArgument(dataFetchingEnvironment, SchemaFieldConstants.QUERY_ID),
            SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaFieldConstants.FROM)),
            SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaFieldConstants.TO))
        );
    }

    @SchemaMapping(typeName = "TargetAlignment", field = GraphqlSchemaMapping.TARGET_SEQUENCE)
    public Mono<String> getTargetSequence(TargetAlignment targetAlignment){
        return SequenceCollector.getSequence( targetAlignment.getTargetId() );
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment, SequenceAlignments sequenceAlignments){
        return SequenceCollector.getSequence( getArgument(dataFetchingEnvironment, SchemaFieldConstants.QUERY_ID) );
    }

    /*@SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }*/

}
