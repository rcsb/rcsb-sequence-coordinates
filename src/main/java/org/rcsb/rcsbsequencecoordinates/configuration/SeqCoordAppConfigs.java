package org.rcsb.rcsbsequencecoordinates.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "seqcoords")
public class SeqCoordAppConfigs {

    private String mongoDbUriScheme = "mongodb";

    private String readPreference;

    public String getMongoDbUriScheme() {
        return mongoDbUriScheme;
    }

    public void setMongoDbUriScheme(String mongoDbUriScheme) {
        this.mongoDbUriScheme = mongoDbUriScheme;
    }

    public String getReadPreference() {
        return readPreference;
    }

    public void setReadPreference(String readPreference) {
        this.readPreference = readPreference;
    }
}
