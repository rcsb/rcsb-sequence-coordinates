/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.controller;

import org.bson.Document;
import org.rcsb.graphqlschema.query.AnnotationsSubscription;
import org.rcsb.graphqlschema.reference.AnnotationReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.graphqlschema.query.AnnotationsQuery;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.rcsb.collectors.annotations.AnnotationsCollector.getAnnotations;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

@Controller
public class AnnotationsController implements AnnotationsQuery<Flux<Document>> , AnnotationsSubscription<Flux<Document>> {

    @Override
    @QueryMapping(name = SchemaConstants.Query.ANNOTATIONS)
    public Flux<Document> annotations(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.REFERENCE) SequenceReference reference,
            @Argument(name = SchemaConstants.Param.SOURCES) List<AnnotationReference> annotationReferences
    ) {
        return getAnnotations(queryId, reference, annotationReferences);
    }

    @Override
    @QueryMapping(name = SchemaConstants.Query.GROUP_ANNOTATIONS)
    public Flux<Document> groupAnnotations(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group,
            @Argument(name = SchemaConstants.Param.SOURCES) List<AnnotationReference> annotationReferences
    ) {
        return getAnnotations(groupId, group, annotationReferences);
    }

    @Override
    @SubscriptionMapping(name = SchemaConstants.Subscription.ANNOTATIONS_SUBSCRIPTION)
    public Flux<Document> annotationsSubscription(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.REFERENCE) SequenceReference reference,
            @Argument(name = SchemaConstants.Param.SOURCES) List<AnnotationReference> annotationReferences
    ) {
        return getAnnotations(queryId, reference, annotationReferences);
    }

    @Override
    @SubscriptionMapping(name = SchemaConstants.Subscription.GROUP_ANNOTATIONS_SUBSCRIPTION)
    public Flux<Document> groupAnnotationsSubscription(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group,
            @Argument(name = SchemaConstants.Param.SOURCES) List<AnnotationReference> annotationReferences
    ) {
        return getAnnotations(groupId, group, annotationReferences);
    }
}
