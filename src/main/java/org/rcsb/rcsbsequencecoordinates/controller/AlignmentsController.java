package org.rcsb.rcsbsequencecoordinates.controller;

import graphql.schema.DataFetchingEnvironment;
import org.bson.Document;
import org.rcsb.graphqlschema.query.AlignmentsQuery;
import org.rcsb.graphqlschema.query.AlignmentsSubscription;
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

import static org.rcsb.collectors.sequence.SequenceCollector.getSequence;
import static org.rcsb.collectors.alignments.TargetAlignmentsCollector.getAlignments;
import static org.rcsb.utils.GraphqlMethods.getArgument;
import static org.rcsb.utils.GraphqlMethods.getQueryName;

@Controller
public class AlignmentsController implements AlignmentsQuery<Mono<Document>>, AlignmentsSubscription<Flux<Document>> {

    @QueryMapping(name = SchemaConstants.Query.ALIGNMENT)
    public Mono<Document> alignment(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.FROM) SequenceReference from,
            @Argument(name = SchemaConstants.Param.TO) SequenceReference to
    ) {
        return Mono.just(new Document());
    }

    @QueryMapping(name = SchemaConstants.Query.GROUP_ALIGNMENT)
    public Mono<Document> group_alignment(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group
    ) {
        return Mono.just(new Document());
    }

    @SubscriptionMapping(name = SchemaConstants.Query.ALIGNMENT)
    public Flux<Document> alignment_subscription(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.FROM) SequenceReference from,
            @Argument(name = SchemaConstants.Param.TO) SequenceReference to
    ) {
        return getAlignments(queryId, from, to);
    }

    @SubscriptionMapping(name = SchemaConstants.Subscription.GROUP_ALIGNMENT_SUBSCRIPTION)
    public Flux<Document> group_alignment_subscription(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group
    ) {
        return getAlignments(groupId, group);
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.TARGET_ALIGNMENT)
    public Flux<Document> getTargetAlignments(DataFetchingEnvironment dataFetchingEnvironment){
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.ALIGNMENT))
            return getAlignments(
                getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.FROM)),
                SequenceReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.TO))
            );
        if(getQueryName(dataFetchingEnvironment).equals(SchemaConstants.Query.GROUP_ALIGNMENT))
            return getAlignments(
                getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP_ID),
                GroupReference.valueOf(getArgument(dataFetchingEnvironment, SchemaConstants.Param.GROUP))
            );
        throw new RuntimeException(String.format("Undefined end point query %s", getQueryName(dataFetchingEnvironment)));
    }

    @SchemaMapping(typeName = "TargetAlignment", field = GraphqlSchemaMapping.TARGET_SEQUENCE)
    public Mono<String> getTargetSequence(Document targetAlignment){
        return getSequence( targetAlignment.getString(CoreConstants.TARGET_ID) );
    }

    @SchemaMapping(typeName = "SequenceAlignments", field = GraphqlSchemaMapping.QUERY_SEQUENCE)
    public Mono<String> getQuerySequence(DataFetchingEnvironment dataFetchingEnvironment){
        return getSequence( getArgument(dataFetchingEnvironment, SchemaConstants.Param.QUERY_ID) );
    }

    /*@SchemaMapping(field = GraphqlSchemaMapping.ALIGNMENT_LOGO)
    public Mono<List<List<AlignmentLogo>>> getAlignmentLogo(SequenceAlignments sequenceAlignments){
        return Mono.justOrEmpty(new ArrayList<>());
    }*/

}
