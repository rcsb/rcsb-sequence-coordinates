package org.rcsb.graphqlschema.query;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAlignments;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAnnotations;

import java.util.List;

public class ServiceQueries implements
        AlignmentsQuery<SequenceAlignments>,
        AnnotationsQuery<List<SequenceAnnotations>> {

    @GraphQLQuery(name = "alignments", description = "Get sequence alignments")
    public SequenceAlignments alignments(
            @GraphQLArgument(name = "queryId", description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = "from", description = "Query sequence database") SequenceReference.@GraphQLNonNull ReferenceName from,
            @GraphQLArgument(name = "to", description = "Target Sequence database") SequenceReference.@GraphQLNonNull ReferenceName to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = "group_alignment", description = "Get group alignments")
    public SequenceAlignments group_alignment(
            @GraphQLArgument(name = "groupId", description = "Database group identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = "group", description = "Target Sequence database") GroupReference.@GraphQLNonNull ReferenceName to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = "annotations", description = "Get sequence annotations")
    public List<SequenceAnnotations> annotations(
            @GraphQLArgument(name = "queryId", description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = "reference", description = "Query sequence database") SequenceReference.@GraphQLNonNull ReferenceName reference,
            @GraphQLArgument(name = "sources", description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

    @GraphQLQuery(name = "group_annotations", description = "Get group annotations")
    public List<SequenceAnnotations> group_annotations(
            @GraphQLArgument(name = "groupId", description = "Database sequence identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = "group", description = "Query sequence database") GroupReference.@GraphQLNonNull ReferenceName group,
            @GraphQLArgument(name = "sources", description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

}
