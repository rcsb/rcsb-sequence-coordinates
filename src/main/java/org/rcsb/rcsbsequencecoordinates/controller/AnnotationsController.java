package org.rcsb.rcsbsequencecoordinates.controller;

import org.rcsb.graphqlschema.query.AnnotationsConstants;
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

    @QueryMapping(name = AnnotationsConstants.ANNOTATIONS)
    public Flux<SequenceAnnotations> annotations(
            @Argument(name = AnnotationsConstants.QUERY_ID) String queryId,
            @Argument(name = AnnotationsConstants.REFERENCE) SequenceReference.ReferenceName reference,
            @Argument(name = AnnotationsConstants.SOURCES) List<SequenceAnnotations.Source> sources
    ) {
        SequenceAnnotations annotations = new SequenceAnnotations();
        return Flux.fromArray(new SequenceAnnotations[] {annotations});
    }

    @QueryMapping(name = AnnotationsConstants.GROUP_ANNOTATIONS)
    public Flux<SequenceAnnotations> group_annotations(
            @Argument(name = AnnotationsConstants.GROUP_ID) String groupId,
            @Argument(name = AnnotationsConstants.GROUP) GroupReference.ReferenceName group,
            @Argument(name = AnnotationsConstants.SOURCES) List<SequenceAnnotations.Source> sources
    ) {
        SequenceAnnotations annotations = new SequenceAnnotations();
        return Flux.fromArray(new SequenceAnnotations[] {annotations});
    }

}
