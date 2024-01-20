package org.rcsb.utils;

import java.util.Properties;

import org.rcsb.common.config.ConfigProfileManager;
import org.rcsb.common.config.PropertiesReader;

public class Parameters {

    private static PropertiesReader propsReader;
    private static Properties buildProps;

    public static PropertiesReader getPropertiesReader() {
        if (propsReader == null) {
            propsReader = ConfigProfileManager.getBorregoAppPropertiesReader();
        }
        return propsReader;
    }
    public static Properties getBuildProperties() {
        if (buildProps == null) {
            buildProps = ConfigProfileManager.getBuildProperties();
        }
        return buildProps;
    }

}

