package com.sv.core.config;

import com.sv.core.Constants;
import com.sv.core.Utils;
import com.sv.core.logger.MyLogger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * File that saves/loads configuration
 */
public class DefaultConfigs {

    private URL propUrl;
    private final String[] config;

    String propFileName = "./conf.config";
    private final Properties configs = new Properties();
    private final MyLogger logger;

    /**
     * Constructor with logger and string array
     *
     * @param logger Logger
     * @param config String array to store in config file
     */
    public DefaultConfigs(MyLogger logger, String[] config) {
        this.logger = logger;
        this.config = config;
        initialize();
    }

    /**
     * Initialization
     */
    public void initialize() {
        readConfig();
    }

    /**
     * Return configuration from property file
     *
     * @param name config name
     * @return config value
     */
    public boolean getBooleanConfig(String name) {
        return Boolean.parseBoolean(getConfig(name));
    }

    /**
     * Return configuration from property file
     *
     * @param name config name
     * @return config value
     */
    public int getIntConfig(String name) {
        return Integer.parseInt(getConfig(name));
    }

    /**
     * Return configuration from property file
     *
     * @param name config name
     * @return config value
     */
    public String getConfig(String name) {
        if (configs.containsKey(name))
            return configs.getProperty(name);
        return Constants.EMPTY;
    }

    private void readConfig() {
        logger.log("Loading properties from path: " + propFileName);
        try (InputStream is = Files.newInputStream(Paths.get(propFileName))) {
            propUrl = Paths.get(propFileName).toUri().toURL();
            configs.load(is);
        } catch (Exception e) {
            logger.log("Error in loading properties via file path, trying class loader.");
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(propFileName)) {
                propUrl = Paths.get(propFileName).toUri().toURL();
                configs.load(is);
            } catch (IOException ioException) {
                logger.log("Error in loading properties via class loader.");
            }
        }
        logger.log("Prop url calculated as: " + propUrl);
    }

    /**
     * Save config in property file
     *
     * @param obj Calling class that has getters
     */
    public void saveConfig(Object obj) {
        logger.log("Saving properties at " + propUrl.getPath());
        configs.clear();
        for (String cfg : config) {
            configs.put(cfg, Utils.callMethod(obj, "get" + cfg, null, logger));
        }
        logger.log("Config is " + configs);
        try {
            configs.store(new FileOutputStream(propUrl.getPath()), null);
        } catch (IOException e) {
            logger.log("Error in saving properties.");
        }
    }
}
