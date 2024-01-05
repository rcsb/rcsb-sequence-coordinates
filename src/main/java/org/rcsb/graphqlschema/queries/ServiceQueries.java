package org.rcsb.graphqlschema.queries;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAlignments;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAnnotations;

public class ServiceQueries implements SequenceAlignmentsQuery<SequenceAlignments>, SequenceAnnotationsQuery<SequenceAnnotations> {

    @GraphQLQuery(name = "alignments", description = "Get sequence alignments")
    public SequenceAlignments alignments(@GraphQLArgument(name = "queryId", description = "Database sequence identifier") @GraphQLNonNull String queryId) {
        return new SequenceAlignments();
    }

    @GraphQLQuery(name = "annotations", description = "Get sequence annotations")
    public SequenceAnnotations annotations(@GraphQLArgument(name = "queryId", description = "Database sequence identifier") @GraphQLNonNull String queryId) {
        return new SequenceAnnotations();
    }

}
