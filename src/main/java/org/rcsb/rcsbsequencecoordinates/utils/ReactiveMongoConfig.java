package org.rcsb.rcsbsequencecoordinates.utils;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.rcsb.rcsbsequencecoordinates.configuration.SeqCoordAppConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
public class ReactiveMongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveMongoConfig.class);

    private final SeqCoordAppConfigs seqCoordAppConfigs;

    public ReactiveMongoConfig(SeqCoordAppConfigs seqCoordAppConfigs) {
        this.seqCoordAppConfigs = seqCoordAppConfigs;
    }

    @Bean
    public MongoConnectionDetails mongoConnectionDetails(MongoProperties props) {
        if (props.getUsername().isBlank()) {
            logger.warn("Username read from environment variable is blank");
        }
        if (Arrays.toString(props.getPassword()).isBlank()) {
            logger.warn("Password read from environment variable is blank");
        }
        String userEncoded = URLEncoder.encode(props.getUsername(), StandardCharsets.UTF_8);
        String pwdEncoded = URLEncoder.encode(new String(props.getPassword()), StandardCharsets.UTF_8);
        String extraParams = buildParamsString(props.getSsl() != null && props.getSsl().isEnabled(), props.getReplicaSetName(), seqCoordAppConfigs.getReadPreference());
        String uri = String.format("%s://%s:%s@%s%s/%s%s",
                seqCoordAppConfigs.getMongoDbUriScheme(),
                userEncoded,
                pwdEncoded,
                props.getHost(),
                props.getPort()!=null && props.getPort()>0 ? ":" + props.getPort() : "",
                props.getAuthenticationDatabase(),
                extraParams);
        logger.info("Reactive MongoDB connection final connection URI: {}", getUriRedacted(uri, userEncoded, pwdEncoded));
        return () -> new ConnectionString(uri);
    }

    @Bean
    public MongoClient reactiveMongoClient(MongoConnectionDetails mongoConnectionDetails) {
        return MongoClients.create(mongoConnectionDetails.getConnectionString());
    }

    @Bean
    public ReactiveMongoResource reactiveMongoResource(MongoClient mongoClient, MongoProperties mongoProperties) {
        return new ReactiveMongoResource(mongoClient, mongoProperties.getDatabase());
    }

    private String getUriRedacted(String uri, String userEncoded, String pwdEncoded) {
        return uri.replace(userEncoded, "********").replace(pwdEncoded, "********");
    }

    private String buildParamsString(boolean sslEnabled, String replicaSet, String readPreference) {
        String repSetStr = "";
        if (replicaSet != null && !replicaSet.isEmpty()) {
            repSetStr = "&replicaSet=" + replicaSet;
        }
        String readPrefStr = "";
        if (readPreference != null && !readPreference.isEmpty()) {
            readPrefStr = "&readPreference=" + readPreference;
        }
        return "?ssl=" + sslEnabled + repSetStr + readPrefStr;
    }
}