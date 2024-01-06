package org.rcsb.rcsbsequencecoordinates.controller;

import org.rcsb.graphqlschema.query.AnnotationsQuery;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.rcsbsequencecoordinates.auto.SequenceAnnotations;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class AnnotationsController implements AnnotationsQuery<Flux<SequenceAnnotations>> {

    @QueryMapping
    public Flux<SequenceAnnotations> annotations(
            @Argument String queryId,
            @Argument SequenceReference.ReferenceName reference,
            @Argument List<SequenceAnnotations.Source> sources
    ) {
        SequenceAnnotations annotations = new SequenceAnnotations();
        return Flux.fromArray(new SequenceAnnotations[] {annotations});
    }

    @QueryMapping
    public Flux<SequenceAnnotations> group_annotations(
            @Argument String groupId,
            @Argument GroupReference.ReferenceName group,
            @Argument List<SequenceAnnotations.Source> sources
    ) {
        SequenceAnnotations annotations = new SequenceAnnotations();
        return Flux.fromArray(new SequenceAnnotations[] {annotations});
    }

}
