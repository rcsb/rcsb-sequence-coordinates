package org.rcsb.graphqlschema.query;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.auto.SequenceAlignments;
import org.rcsb.mojave.auto.SequenceAnnotations;

import java.util.List;

public class ServiceQueries implements
        AlignmentsQuery<SequenceAlignments>,
        AnnotationsQuery<List<SequenceAnnotations>> {

    @GraphQLQuery(name = AnnotationsConstants.ALIGNMENTS, description = "Get sequence alignments")
    public SequenceAlignments alignments(
            @GraphQLArgument(name = AnnotationsConstants.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = AnnotationsConstants.FROM, description = "Query sequence database") SequenceReference.@GraphQLNonNull ReferenceName from,
            @GraphQLArgument(name = AnnotationsConstants.TO, description = "Target Sequence database") SequenceReference.@GraphQLNonNull ReferenceName to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = AnnotationsConstants.GROUP_ALIGNMENTS, description = "Get group alignments")
    public SequenceAlignments group_alignments(
            @GraphQLArgument(name = AnnotationsConstants.GROUP_ID, description = "Database group identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = AnnotationsConstants.GROUP, description = "Target Sequence database") GroupReference.@GraphQLNonNull ReferenceName to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = AnnotationsConstants.ANNOTATIONS, description = "Get sequence annotations")
    public List<SequenceAnnotations> annotations(
            @GraphQLArgument(name = AnnotationsConstants.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = AnnotationsConstants.REFERENCE, description = "Query sequence database") SequenceReference.@GraphQLNonNull ReferenceName reference,
            @GraphQLArgument(name = AnnotationsConstants.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

    @GraphQLQuery(name = AnnotationsConstants.GROUP_ANNOTATIONS, description = "Get group annotations")
    public List<SequenceAnnotations> group_annotations(
            @GraphQLArgument(name = AnnotationsConstants.GROUP_ID, description = "Database sequence identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = AnnotationsConstants.GROUP, description = "Query sequence database") GroupReference.@GraphQLNonNull ReferenceName group,
            @GraphQLArgument(name = AnnotationsConstants.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

}
