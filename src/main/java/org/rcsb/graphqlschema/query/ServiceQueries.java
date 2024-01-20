package org.rcsb.graphqlschema.query;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.response.SequenceAlignments;
import org.rcsb.graphqlschema.schema.SchemaFieldConstants;
import org.rcsb.mojave.auto.SequenceAnnotations;

import java.util.List;

public class ServiceQueries implements
        AlignmentsQuery<SequenceAlignments>,
        AnnotationsQuery<List<SequenceAnnotations>> {

    @GraphQLQuery(name = SchemaFieldConstants.ALIGNMENTS, description = "Get sequence alignments")
    public SequenceAlignments alignments(
            @GraphQLArgument(name = SchemaFieldConstants.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaFieldConstants.FROM, description = "Query sequence database") @GraphQLNonNull SequenceReference from,
            @GraphQLArgument(name = SchemaFieldConstants.TO, description = "Target Sequence database") @GraphQLNonNull SequenceReference to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = SchemaFieldConstants.GROUP_ALIGNMENTS, description = "Get group alignments")
    public SequenceAlignments group_alignments(
            @GraphQLArgument(name = SchemaFieldConstants.GROUP_ID, description = "Database group identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaFieldConstants.GROUP, description = "Target Sequence database") @GraphQLNonNull GroupReference to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = SchemaFieldConstants.ANNOTATIONS, description = "Get sequence annotations")
    public List<SequenceAnnotations> annotations(
            @GraphQLArgument(name = SchemaFieldConstants.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaFieldConstants.REFERENCE, description = "Query sequence database") @GraphQLNonNull SequenceReference reference,
            @GraphQLArgument(name = SchemaFieldConstants.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

    @GraphQLQuery(name = SchemaFieldConstants.GROUP_ANNOTATIONS, description = "Get group annotations")
    public List<SequenceAnnotations> group_annotations(
            @GraphQLArgument(name = SchemaFieldConstants.GROUP_ID, description = "Database sequence identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaFieldConstants.GROUP, description = "Query sequence database") @GraphQLNonNull GroupReference group,
            @GraphQLArgument(name = SchemaFieldConstants.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

}
