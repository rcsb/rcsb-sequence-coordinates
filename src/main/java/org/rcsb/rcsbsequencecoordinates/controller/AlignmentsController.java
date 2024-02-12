/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.bson.Document;
import org.rcsb.collectors.alignments.AlignmentsCollector;
import org.rcsb.graphqlschema.service.AlignmentsQuery;
import org.rcsb.graphqlschema.service.AlignmentsSubscription;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.CoreConstants;
import org.rcsb.rcsbsequencecoordinates.configuration.GraphqlSchemaMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.rcsb.collectors.sequence.SequenceCollector.getSequence;
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
    @QueryMapping(name = SchemaConstants.Query.ALIGNMENT)
    public Mono<Document> alignment(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.FROM) SequenceReference from,
            @Argument(name = SchemaConstants.Param.TO) SequenceReference to,
            @Argument(name = SchemaConstants.Param.RANGE) List<Integer> range
    ) {
        return Mono.just(new Document());
    }

    @Override
    @QueryMapping(name = SchemaConstants.Query.GROUP_ALIGNMENT)
    public Mono<Document> groupAlignment(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group,
            @Argument(name = SchemaConstants.Param.GROUP_FILTER) List<String> filter
    ) {
        return Mono.just(new Document());
    }

    @Override
    @SubscriptionMapping(name = SchemaConstants.Query.ALIGNMENT)
    public Flux<Document> alignmentSubscription(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.FROM) SequenceReference from,
            @Argument(name = SchemaConstants.Param.TO) SequenceReference to,
            @Argument(name = SchemaConstants.Param.RANGE) List<Integer> range
    ) {
        return AlignmentsCollector
                .request(queryId, from, to)
                .range(range)
                .get();
    }

    @Override
    @SubscriptionMapping(name = SchemaConstants.Subscription.GROUP_ALIGNMENT_SUBSCRIPTION)
    public Flux<Document> groupAlignmentSubscription(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group,
            @Argument(name = SchemaConstants.Param.GROUP_FILTER) List<String> filter
    ) {
        return AlignmentsCollector
                .request(groupId, group)
                .filter(filter)
                .get();
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.TARGET_ALIGNMENT)
    public Flux<Document> getTargetAlignments(
            DataFetchingEnvironment dataFetchingEnvironment,
            @Argument(name = SchemaConstants.Param.FIRST) Integer first,
            @Argument(name = SchemaConstants.Param.OFFSET) Integer offset
    ){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.ALIGNMENT))
            return AlignmentsCollector
                    .request(
                        getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                        SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM)),
                        SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
                    )
                    .range(
                            getArgument(dataFetchingEnvironment, SchemaConstants.Param.RANGE)
                    )
                    .get();
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENT))
            return AlignmentsCollector
                    .request(
                        getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_ID),
                        GroupReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP))
                    ).filter(
                            getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_FILTER)
                    )
                    .get();
        throw new RuntimeException(String.format("Undefined end point query %s", getQueryName(dataFetchingEnvironment)));
    }

    @SchemaMapping(typeName = "TargetAlignment", field = GraphqlSchemaMapping.TARGET_SEQUENCE)
    public Mono<String> getTargetSequence(DataFetchingEnvironment dataFetchingEnvironment, Document targetAlignment){
        if(
                getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENT) ||
                getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Subscription.GROUP_ALIGNMENT_SUBSCRIPTION)
        )
            return getSequence(
                    targetAlignment.getString(CoreConstants.TARGET_ID),
                    SequenceReference.PDB_ENTITY
            );
        return getSequence(
                targetAlignment.getString(CoreConstants.TARGET_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
        );
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENT))
            return null;
        return getSequence(
                getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM))
        );
    }

    /*@SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }*/

}
