package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.rcsb.graphqlschema.query.AnnotationsConstants;
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

    @QueryMapping(name = AnnotationsConstants.ALIGNMENTS)
    public Mono<SequenceAlignments> alignments(
            @Argument(name = AnnotationsConstants.QUERY_ID) String queryId,
            @Argument(name = AnnotationsConstants.FROM) SequenceReference.ReferenceName from,
            @Argument(name = AnnotationsConstants.TO) SequenceReference.ReferenceName to
    ) {
        SequenceAlignments alignment = new SequenceAlignments();
        alignment.setAlignmentLength(1000);
        return Mono.justOrEmpty(alignment);
    }

    @QueryMapping(name = AnnotationsConstants.GROUP_ALIGNMENTS)
    public Mono<SequenceAlignments> group_alignments(
            @Argument(name = AnnotationsConstants.GROUP_ID) String groupId,
            @Argument(name = AnnotationsConstants.GROUP) GroupReference.ReferenceName group
    ) {
        SequenceAlignments alignment = new SequenceAlignments();
        alignment.setAlignmentLength(1000);
        return Mono.justOrEmpty(alignment);
    }

    @SchemaMapping(field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment, SequenceAlignments sequenceAlignments){
        String queryId = dataFetchingEnvironment.getExecutionStepInfo().getParent().getArgument(AnnotationsConstants.QUERY_ID);
        return Mono.justOrEmpty("ABABAB");
    }

    @SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }

}
