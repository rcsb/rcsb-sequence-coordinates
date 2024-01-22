package org.rcsb.graphqlschema.query;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.response.SequenceAlignments;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.auto.SequenceAnnotations;

import java.util.List;

public class ServiceQueries implements
        AlignmentsQuery<SequenceAlignments>,
        AnnotationsQuery<List<SequenceAnnotations>> {

    @GraphQLQuery(name = SchemaConstants.Query.ALIGNMENT, description = "Get sequence alignments")
    public SequenceAlignments alignments(
            @GraphQLArgument(name = SchemaConstants.Param.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaConstants.Param.FROM, description = "Query sequence database") @GraphQLNonNull SequenceReference from,
            @GraphQLArgument(name = SchemaConstants.Param.TO, description = "Target Sequence database") @GraphQLNonNull SequenceReference to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = SchemaConstants.Query.GROUP_ALIGNMENT, description = "Get group alignments")
    public SequenceAlignments group_alignments(
            @GraphQLArgument(name = SchemaConstants.Param.GROUP_ID, description = "Database group identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaConstants.Param.GROUP, description = "Target Sequence database") @GraphQLNonNull GroupReference to
    ) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = SchemaConstants.Query.ANNOTATIONS, description = "Get sequence annotations")
    public List<SequenceAnnotations> annotations(
            @GraphQLArgument(name = SchemaConstants.Param.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaConstants.Param.REFERENCE, description = "Query sequence database") @GraphQLNonNull SequenceReference reference,
            @GraphQLArgument(name = SchemaConstants.Param.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

    @GraphQLQuery(name = SchemaConstants.Query.GROUP_ANNOTATIONS, description = "Get group annotations")
    public List<SequenceAnnotations> group_annotations(
            @GraphQLArgument(name = SchemaConstants.Param.GROUP_ID, description = "Database sequence identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaConstants.Param.GROUP, description = "Query sequence database") @GraphQLNonNull GroupReference group,
            @GraphQLArgument(name = SchemaConstants.Param.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<SequenceAnnotations.Source> source
    ) {
        return List.of(new SequenceAnnotations());
    }

}
