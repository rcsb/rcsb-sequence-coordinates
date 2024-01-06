package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.rcsbsequencecoordinates.auto.AlignmentLogo;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAlignments;
import org.rcsb.rcsbsequencecoordinates.configuration.GraphqlSchemaMapping;
import org.rcsb.graphqlschema.query.AlignmentsQuery;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AlignmentsController implements AlignmentsQuery<Mono<SequenceAlignments>> {

    @QueryMapping
    public Mono<SequenceAlignments> alignments(
            @Argument String queryId,
            @Argument SequenceReference.ReferenceName from,
            @Argument SequenceReference.ReferenceName to
    ) {
        SequenceAlignments alignment = new SequenceAlignments();
        alignment.setAlignmentLength(1000);
        return Mono.justOrEmpty(alignment);
    }

    @QueryMapping
    public Mono<SequenceAlignments> group_alignment(
            @Argument String queryId,
            @Argument GroupReference.ReferenceName group
    ) {
        SequenceAlignments alignment = new SequenceAlignments();
        alignment.setAlignmentLength(1000);
        return Mono.justOrEmpty(alignment);
    }

    @SchemaMapping(field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment, SequenceAlignments sequenceAlignments){
        // TODO Clever way to encode queryId to keep consistency
        String queryId = dataFetchingEnvironment.getExecutionStepInfo().getParent().getArgument("queryId");
        return Mono.justOrEmpty("ABABAB");
    }

    @SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }

}
