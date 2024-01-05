package org.rcsb.rcsbsequencecoordinates.controller;

import org.rcsb.graphqlschema.queries.SequenceAnnotationsQuery;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAnnotations;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class AnnotationsController implements SequenceAnnotationsQuery<Mono<SequenceAnnotations>> {

    @QueryMapping
    public Mono<SequenceAnnotations> annotations(@Argument String queryId) {
        SequenceAnnotations annotations = new SequenceAnnotations();
        return Mono.justOrEmpty(annotations);
    }

}
