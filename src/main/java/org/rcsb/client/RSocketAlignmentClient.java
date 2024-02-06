/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.client;

import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.client.RSocketGraphQlClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

class RSocketAlignmentClient {

    private static final Logger logger = LoggerFactory.getLogger(RSocketAlignmentClient.class);
    private final RSocketGraphQlClient client;
    private RSocketAlignmentClient(){
        URI uri = URI.create("ws://132.249.213.162:8080/rsocket");
        WebsocketClientTransport transport = WebsocketClientTransport.create(uri);
        client = RSocketGraphQlClient.builder()
               .clientTransport(transport)
               .build();
    }
    private void request() throws IOException {
        String query = """
          subscription groupSubscription {
            group_alignment_subscription(
              group: SEQUENCE_IDENTITY
              groupId: "2_30"
            ){
              target_id
              target_sequence
              aligned_regions{
                query_begin
                query_end
                target_begin
                target_end
              }
            }
          }
        """;
        logger.info("Requesting: {}", query);
        long timeS = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
        client.document(query)
                .executeSubscription()
                .mapNotNull(r->r.toEntity(Document.class))
                .doOnComplete(()->{
                    long timeE = System.currentTimeMillis();
                    logger.info("Retrieved {} documents in {}", count.get(), DurationFormatUtils.formatPeriod(timeS, timeE, "HH:mm:ss:SS"));
                    System.exit(0);
                })
                .subscribe(d->{
                    if(count.getAndIncrement() == 0) {
                        long timeE = System.currentTimeMillis();
                        logger.info("Retrieved first document in {}", DurationFormatUtils.formatPeriod(timeS, timeE, "HH:mm:ss:SS"));
                    }
                });

        while(true) System.in.read();
    }

    public static void main(String[] args) throws IOException {
        RSocketAlignmentClient me = new RSocketAlignmentClient();
        me.request();
    }

}
