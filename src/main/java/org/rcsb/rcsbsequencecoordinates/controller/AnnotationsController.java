package org.rcsb.rcsbsequencecoordinates.controller;

import org.rcsb.graphqlschema.schema.SchemaConstants;
import org.rcsb.graphqlschema.query.AnnotationsQuery;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.mojave.auto.SequenceAnnotations;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class AnnotationsController implements AnnotationsQuery<Flux<SequenceAnnotations>> {

    @QueryMapping(name = SchemaConstants.Query.ANNOTATIONS)
    public Flux<SequenceAnnotations> annotations(
            @Argument(name = SchemaConstants.Param.QUERY_ID) String queryId,
            @Argument(name = SchemaConstants.Param.REFERENCE) SequenceReference reference,
            @Argument(name = SchemaConstants.Param.SOURCES) List<SequenceAnnotations.Source> sources
    ) {
        SequenceAnnotations annotations = new SequenceAnnotations();
        return Flux.fromArray(new SequenceAnnotations[] {annotations});
    }

    @QueryMapping(name = SchemaConstants.Query.GROUP_ANNOTATIONS)
    public Flux<SequenceAnnotations> group_annotations(
            @Argument(name = SchemaConstants.Param.GROUP_ID) String groupId,
            @Argument(name = SchemaConstants.Param.GROUP) GroupReference group,
            @Argument(name = SchemaConstants.Param.SOURCES) List<SequenceAnnotations.Source> sources
    ) {
        SequenceAnnotations annotations = new SequenceAnnotations();
        return Flux.fromArray(new SequenceAnnotations[] {annotations});
    }

}
