package org.rcsb.rcsbsequencecoordinates.controller;

import org.rcsb.rcsbsequencecoordinates.auto.AlignmentLogo;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAlignments;
import org.rcsb.rcsbsequencecoordinates.configuration.GraphqlSchemaMapping;
import org.rcsb.graphqlschema.queries.SequenceAlignmentsQuery;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AlignmentsController implements SequenceAlignmentsQuery<Mono<SequenceAlignments>> {
    @QueryMapping
    public Mono<SequenceAlignments> alignments(@Argument String queryId) {
        SequenceAlignments alignment = new SequenceAlignments();
        alignment.setAlignmentLength(1000);
        return Mono.justOrEmpty(alignment);
    }

    @SchemaMapping(field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty("ABABAB");
    }

    @SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }

}
