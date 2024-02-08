/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLSubscription;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.response.SequenceAlignments;
import org.rcsb.graphqlschema.response.TargetAlignment;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.mojave.auto.SequenceAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class ServiceQueries implements
        AlignmentsQuery<SequenceAlignments>,
        AlignmentsSubscription<List<TargetAlignment>>,
        AnnotationsQuery<List<SequenceAnnotations>>,
        AnnotationsSubscription<List<SequenceAnnotations>> {

    @Override
    @GraphQLQuery(name = SchemaConstants.Query.ALIGNMENT, description = "Get sequence alignments")
    public SequenceAlignments alignment(
            @GraphQLArgument(name = SchemaConstants.Param.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaConstants.Param.FROM, description = "Query sequence database") @GraphQLNonNull SequenceReference from,
            @GraphQLArgument(name = SchemaConstants.Param.TO, description = "Target Sequence database") @GraphQLNonNull SequenceReference to,
            @GraphQLArgument(name = SchemaConstants.Param.RANGE, description = "Optional integer list (2-tuple) to filter alignments to a particular region") List<@GraphQLNonNull Integer> range
    ) {
        return new SequenceAlignments();
    }

    @Override
    @GraphQLQuery(name = SchemaConstants.Query.GROUP_ALIGNMENT, description = "Get group alignments")
    public SequenceAlignments groupAlignment(
            @GraphQLArgument(name = SchemaConstants.Param.GROUP_ID, description = "Database group identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaConstants.Param.GROUP, description = "Target Sequence database") @GraphQLNonNull GroupReference to,
            @GraphQLArgument(name = SchemaConstants.Param.FILTER, description = "Optional string list of allowed group member identifiers") List<@GraphQLNonNull String> filter
    ) {
        return new SequenceAlignments();
    }

    @Override
    @GraphQLSubscription(name = SchemaConstants.Subscription.ALIGNMENT_SUBSCRIPTION, description = "Get sequence alignments")
    public List<TargetAlignment> alignmentSubscription(
            @GraphQLArgument(name = SchemaConstants.Param.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaConstants.Param.FROM, description = "Query sequence database") @GraphQLNonNull SequenceReference from,
            @GraphQLArgument(name = SchemaConstants.Param.TO, description = "Target Sequence database") @GraphQLNonNull SequenceReference to,
            @GraphQLArgument(name = SchemaConstants.Param.RANGE, description = "Optional integer list (2-tuple) to filter alignments to a particular region") List<@GraphQLNonNull Integer> range
    ) {
        return new ArrayList<>();
    }

    @Override
    @GraphQLSubscription(name = SchemaConstants.Subscription.GROUP_ALIGNMENT_SUBSCRIPTION, description = "Get group alignments")
    public List<TargetAlignment> groupAlignmentSubscription(
            @GraphQLArgument(name = SchemaConstants.Param.GROUP_ID, description = "Database group identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaConstants.Param.GROUP, description = "Target Sequence database") @GraphQLNonNull GroupReference to,
            @GraphQLArgument(name = SchemaConstants.Param.FILTER, description = "Optional string list of allowed group member identifiers") List<@GraphQLNonNull String> filter
    ) {
        return new ArrayList<>();
    }

    @Override
    @GraphQLQuery(name = SchemaConstants.Query.ANNOTATIONS, description = "Get sequence annotations")
    public List<SequenceAnnotations> annotations(
            @GraphQLArgument(name = SchemaConstants.Param.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaConstants.Param.REFERENCE, description = "Query sequence database") @GraphQLNonNull SequenceReference reference,
            @GraphQLArgument(name = SchemaConstants.Param.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<AnnotationReference> source,
            @GraphQLArgument(name = SchemaConstants.Param.RANGE, description = "Optional integer list (2-tuple) to filter annotations to a particular region") List<@GraphQLNonNull Integer> range
    ) {
        return List.of(new SequenceAnnotations());
    }

    @Override
    @GraphQLQuery(name = SchemaConstants.Query.GROUP_ANNOTATIONS, description = "Get group annotations")
    public List<SequenceAnnotations> groupAnnotations(
            @GraphQLArgument(name = SchemaConstants.Param.GROUP_ID, description = "Database sequence identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaConstants.Param.GROUP, description = "Query sequence database") @GraphQLNonNull GroupReference group,
            @GraphQLArgument(name = SchemaConstants.Param.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<AnnotationReference> source
    ) {
        return List.of(new SequenceAnnotations());
    }

    @Override
    @GraphQLSubscription(name = SchemaConstants.Subscription.ANNOTATIONS_SUBSCRIPTION, description = "Get sequence annotations")
    public List<SequenceAnnotations> annotationsSubscription(
            @GraphQLArgument(name = SchemaConstants.Param.QUERY_ID, description = "Database sequence identifier") @GraphQLNonNull String queryId,
            @GraphQLArgument(name = SchemaConstants.Param.REFERENCE, description = "Query sequence database") @GraphQLNonNull SequenceReference reference,
            @GraphQLArgument(name = SchemaConstants.Param.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<AnnotationReference> source,
            @GraphQLArgument(name = SchemaConstants.Param.RANGE, description = "Optional integer list (2-tuple) to filter annotations to a particular region") List<@GraphQLNonNull Integer> range
    ) {
        return List.of(new SequenceAnnotations());
    }

    @Override
    @GraphQLSubscription(name = SchemaConstants.Subscription.GROUP_ANNOTATIONS_SUBSCRIPTION, description = "Get group annotations")
    public List<SequenceAnnotations> groupAnnotationsSubscription(
            @GraphQLArgument(name = SchemaConstants.Param.GROUP_ID, description = "Database sequence identifier") @GraphQLNonNull String groupId,
            @GraphQLArgument(name = SchemaConstants.Param.GROUP, description = "Query sequence database") @GraphQLNonNull GroupReference group,
            @GraphQLArgument(name = SchemaConstants.Param.SOURCES, description = "List defining the annotation collections to be requested") @GraphQLNonNull List<AnnotationReference> source
    ) {
        return List.of(new SequenceAnnotations());
    }

}
