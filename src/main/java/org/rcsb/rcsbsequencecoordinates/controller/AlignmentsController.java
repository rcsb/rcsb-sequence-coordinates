/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.bson.Document;
import org.rcsb.collectors.alignments.AlignmentLengthCollector;
import org.rcsb.collectors.alignments.AlignmentLogoCollector;
import org.rcsb.collectors.alignments.SequenceAlignmentsCollector;
import org.rcsb.graphqlschema.response.SequenceAlignments;
import org.rcsb.graphqlschema.response.TargetAlignments;
import org.rcsb.graphqlschema.service.AlignmentsQuery;
import org.rcsb.graphqlschema.service.AlignmentsSubscription;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.SequenceCoordinatesConstants;
import org.rcsb.rcsbsequencecoordinates.configuration.GraphqlSchemaMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.rcsb.collectors.sequence.SequenceCollector.request;
import static org.rcsb.utils.GraphqlMethods.getArgument;
import static org.rcsb.utils.GraphqlMethods.getQueryName;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

@Controller
public class AlignmentsController implements AlignmentsQuery<Mono<Document>>, AlignmentsSubscription<Flux<Document>> {

    @Override
    @QueryMapping(name = SchemaConstants.Query.ALIGNMENTS)
    public Mono<Document> alignments(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.FROM) SequenceReference from,
            @Argument(name = SchemaConstants.Param.TO) SequenceReference to,
            @Argument(name = SchemaConstants.Param.RANGE) List<Integer> range
    ) {
        return Mono.just(new Document());
    }

    @Override
    @QueryMapping(name = SchemaConstants.Query.GROUP_ALIGNMENTS)
    public Mono<Document> groupAlignments(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group,
            @Argument(name = SchemaConstants.Param.GROUP_FILTER) List<String> filter
    ) {
        return Mono.just(new Document());
    }

    @Override
    @SubscriptionMapping(name = SchemaConstants.Subscription.ALIGNMENTS_SUBSCRIPTION)
    public Flux<Document> alignmentsSubscription(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.FROM) SequenceReference from,
            @Argument(name = SchemaConstants.Param.TO) SequenceReference to,
            @Argument(name = SchemaConstants.Param.RANGE) List<Integer> range
    ) {
        return SequenceAlignmentsCollector
                .request(queryId, from, to)
                .range(range)
                .get();
    }

    @Override
    @SubscriptionMapping(name = SchemaConstants.Subscription.GROUP_ALIGNMENTS_SUBSCRIPTION)
    public Flux<Document> groupAlignmentsSubscription(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group,
            @Argument(name = SchemaConstants.Param.GROUP_FILTER) List<String> filter
    ) {
        return SequenceAlignmentsCollector
                .request(groupId, group)
                .filter(filter)
                .get();
    }

    @SchemaMapping(typeName = SequenceAlignments.CLASS_NAME, field = GraphqlSchemaMapping.TARGET_ALIGNMENTS)
    public Flux<Document> getTargetAlignments(
            DataFetchingEnvironment dataFetchingEnvironment,
            @Argument(name = SchemaConstants.Param.FIRST) Integer first,
            @Argument(name = SchemaConstants.Param.OFFSET) Integer offset
    ){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.ALIGNMENTS))
            return SequenceAlignmentsCollector
                    .request(
                        getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                        SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM)),
                        SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
                    )
                    .range(getArgument(dataFetchingEnvironment, SchemaConstants.Param.RANGE))
                    .page(first, offset)
                    .get();
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENTS))
            return SequenceAlignmentsCollector
                    .request(
                        getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_ID),
                        GroupReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP))
                    )
                    .filter(getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_FILTER))
                    .page(first, offset)
                    .get();
        throw new RuntimeException(String.format("Undefined end point query %s", getQueryName(dataFetchingEnvironment)));
    }

    @SchemaMapping(typeName = TargetAlignments.CLASS_NAME, field = GraphqlSchemaMapping.TARGET_SEQUENCE)
    public Mono<String> getTargetSequence(DataFetchingEnvironment dataFetchingEnvironment, Document targetAlignment){
        if(
                getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENTS) ||
                getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Subscription.GROUP_ALIGNMENTS_SUBSCRIPTION)
        )
            return request(
                    targetAlignment.getString(SequenceCoordinatesConstants.TARGET_ID),
                    SequenceReference.PDB_ENTITY
            );
        return request(
                targetAlignment.getString(SequenceCoordinatesConstants.TARGET_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
        );
    }

    @SchemaMapping(typeName = SequenceAlignments.CLASS_NAME, field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENTS))
            return null;
        return request(
                getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM))
        );
    }

    @SchemaMapping(typeName = SequenceAlignments.CLASS_NAME, field = GraphqlSchemaMapping.ALIGNMENT_LENGTH)
    public Mono<Integer> getAlignmentLength(DataFetchingEnvironment dataFetchingEnvironment){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENTS))
            return AlignmentLengthCollector.request(
                    getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_ID),
                    GroupReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP))
            );
        return AlignmentLengthCollector.request(
                getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM)),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
        );
    }

    @SchemaMapping(typeName = SequenceAlignments.CLASS_NAME, field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<Document>>> getAlignmentLogo(DataFetchingEnvironment dataFetchingEnvironment){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENTS))
            return AlignmentLogoCollector.build()
                    .request(
                            getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_ID),
                            GroupReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP))
                    );
        return AlignmentLogoCollector.build()
                .request(
                        getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                        SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM)),
                        SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
                );
    }

}
