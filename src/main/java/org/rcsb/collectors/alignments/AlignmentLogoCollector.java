/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.alignments;

import org.bson.Document;
import org.rcsb.collectors.sequence.SequenceCollector;
import org.rcsb.collectors.utils.SequenceSymbol;
import org.rcsb.graphqlschema.reference.GroupReference;
import org.rcsb.graphqlschema.reference.SequenceReference;
import org.rcsb.graphqlschema.schema.SchemaConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/13/24, Tuesday
 **/
public class AlignmentLogoCollector {

    private int alignmentLength = -1;
    private AlignmentLogoCollector(){}

    public static AlignmentLogoCollector build(){
        return new AlignmentLogoCollector();
    }

    public Mono<List<List<Document>>> request(String groupId, GroupReference group){
        return buildAlignmentLogo(groupId, group)
                .reduce(this::mergeLogo)
                .map(this::formatLogo);
    }

    public Mono<List<List<Document>>> request(String queryId, SequenceReference from, SequenceReference to){
        return buildAlignmentLogo(queryId, from, to)
                .reduce(this::mergeLogo)
                .map(this::formatLogo);
    }

    private Flux<int[][]> buildAlignmentLogo(String groupId, GroupReference group){
        return AlignmentLengthCollector.request(groupId, group)
                .doOnNext(this::setAlignmentLength)
                .thenMany(processAlignments(groupId, group));
    }

    private Flux<int[][]> buildAlignmentLogo(String queryId, SequenceReference from, SequenceReference to){
        return AlignmentLengthCollector.request(queryId, from, to)
                .doOnNext(this::setAlignmentLength)
                .thenMany(processAlignments(queryId, from, to));
    }

    private Flux<int[][]> processAlignments(String groupId, GroupReference group){
        return SequenceAlignmentsCollector
                .request(groupId, group)
                .get()
                .flatMap(this::buildAlignmentLogo);
    }

    private Flux<int[][]> processAlignments(String queryId, SequenceReference from, SequenceReference to){
        return SequenceAlignmentsCollector
                .request(queryId, from, to)
                .get()
                .flatMap(this::buildAlignmentLogo);
    }

    private Mono<int[][]> buildAlignmentLogo(Document alignment){
        return SequenceCollector.request(alignment.getString(SchemaConstants.Field.TARGET_ID))
                .flatMap(sequence->buildAlignmentLogo(alignment, sequence));
    }

    private Mono<int[][]> buildAlignmentLogo(Document alignment, String sequence){
        return Flux.fromIterable(alignment.getList(SchemaConstants.Field.ALIGNED_REGIONS, Document.class))
                .map(region-> buildRegionLogo(region, sequence))
                .reduce(initLogo(), this::mergeLogo);
    }

    private int[][] buildRegionLogo(Document region, String sequence){
        int[][] logo = zeroLogo();
        char[] seq = sequence.toCharArray();
        int queryBegin = region.getInteger(SchemaConstants.Field.QUERY_BEGIN)-1;
        int targetBegin = region.getInteger(SchemaConstants.Field.TARGET_BEGIN)-1;
        int targetEnd = region.getInteger(SchemaConstants.Field.TARGET_END);
        int qi = queryBegin;
        for(int ti = targetBegin; ti < targetEnd; ti++){
            logo[qi][SequenceSymbol.getIndex(seq[ti])]++;
            logo[qi][SequenceSymbol.getLength()-1]--;
            qi++;
        }
        return logo;
    }

    private List<List<Document>> formatLogo(int[][] logo){
        return Arrays.stream(logo).map(
                aa -> {
                    AtomicInteger n = new AtomicInteger(0);
                    return Arrays.stream(aa).mapToObj(
                            value -> new Document(Map.of(
                                    SchemaConstants.Field.VALUE , value,
                                    SchemaConstants.Field.SYMBOL, SequenceSymbol.getChar(n.getAndIncrement())
                            ))
                    ).toList();
                }
        ).toList();
    }

    private void setAlignmentLength(int length){
        alignmentLength = length;
    }

    private int[][] initLogo(){
        int length = alignmentLength;
        int[][] logo = zeroLogo();
        for(int i=0; i<length; i++){
            logo[i][SequenceSymbol.getLength()-1] = 1;
        }
        return logo;
    }

    private int[][] mergeLogo(int[][] logoA, int[][] logoB) {
        int[][] logo = zeroLogo();
        for (int i = 0; i< logoA.length; i++){
            for (int j = 0; j< logoA[i].length; j++){
                logo[i][j] = logoA[i][j] + logoB[i][j];
            }
        }
        return logo;
    }

    private int[][] zeroLogo(){
        int length = alignmentLength;
        int[][] logo = new int[length][SequenceSymbol.getLength()];
        for(int i=0; i<length; i++){
            Arrays.fill(logo[i], 0);
        }
        return logo;
    }

}
