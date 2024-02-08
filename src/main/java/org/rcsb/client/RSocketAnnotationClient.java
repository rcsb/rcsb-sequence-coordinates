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

class RSocketAnnotationClient {

    private static final Logger logger = LoggerFactory.getLogger(RSocketAnnotationClient.class);
    private final RSocketGraphQlClient client;
    private RSocketAnnotationClient(){
        URI uri = URI.create("ws://132.249.213.162:8080/rsocket");
        WebsocketClientTransport transport = WebsocketClientTransport.create(uri);
        client = RSocketGraphQlClient.builder()
               .clientTransport(transport)
               .build();
    }
    private void request() throws IOException {
        String query = """
                  subscription annotationSubscription {
                          	annotations_subscription(
                              queryId:"P01112"
                              reference:UNIPROT
                              sources:[UNIPROT, PDB_ENTITY, PDB_INSTANCE, PDB_INTERFACE]
                            ){
                            	target_id
                                target_identifiers {
                                  assembly_id
                                  asym_id
                                  entity_id
                                  entry_id
                                  interface_id
                                  interface_partner_index
                                  target_id
                                  uniprot_id
                                }
                              	features{
                                  name
                                  type
                                  feature_positions{
                                    beg_seq_id
                                    end_seq_id
                                    beg_ori_id
                                    end_ori_id
                                    values
                                  }
                                }
                          	}
                          }
                """;
        logger.info("Requesting: {}", query);
        long timeS = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
        client.document(query)
                .executeSubscription()
                .doOnError(e->logger.error("error: {}", e.getMessage()))
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
        RSocketAnnotationClient me = new RSocketAnnotationClient();
        me.request();
    }

}
