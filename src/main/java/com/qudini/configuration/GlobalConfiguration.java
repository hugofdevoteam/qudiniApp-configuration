package com.qudini.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.qudini.configuration.model.YamlConfigurations;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Getter
public class GlobalConfiguration {

    public static final String ENV = "com.qudini.ApplicationEnv";

    public static final YamlConfigurations configuration;

    public static final String DATA_ACTION = "com.qudini.DataGenerationAction";

    static {
        try {
            setEnv();
        } catch (ExceptionInInitializerError e) {
            initialisationErrorLog(e);
        }

    }

    static {
        try {
            setDataAction();
        } catch (ExceptionInInitializerError e) {
            initialisationErrorLog(e);
            System.exit(1);
        }

    }

    static {

        try (

            InputStream inputStream = Files.newInputStream(Paths
                    .get(
                            "src",
                            "main",
                            "resources",
                            "configuration-" + System.getProperty(ENV) + ".yaml"))
        ) {

            configuration = new Yaml().loadAs(inputStream, YamlConfigurations.class);

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    private GlobalConfiguration() {
    }

    private static void setEnv() {

        String env = getEnv();

        if (env == null || env.isEmpty()) {
            log.warn("Missing setup for ENV property. Using local com.qudini.configuration property as default Environment");
            System.setProperty(GlobalConfiguration.ENV, "local");
            env = System.getProperty(GlobalConfiguration.ENV);
        }
        if (!env.equalsIgnoreCase("LOCAL") &&
                !env.equalsIgnoreCase("QA") &&
                !env.equalsIgnoreCase("STG")) {

            // put error string in some error string
            log.error("Wrong value for " + env + " property. The value as inputted was '" + env + "'");
            throw new ExceptionInInitializerError("Wrong value for " + GlobalConfiguration.ENV + " property. The value as inputted was '" + env + "'");
        }
    }

    public static String getEnv(){

        return System.getProperty(ENV);
    }

    private static void setDataAction(){

        String dataGenerationAction = getDataAction();

        if(dataGenerationAction == null || dataGenerationAction.isEmpty()){
            log.error("Missing data generation action (ADD | REMOVE) mandatory parameter. Aborting data generation");
            throw new ExceptionInInitializerError("Missing data generation action (ADD | REMOVE) mandatory parameter. Aborting data generation");

        }else if (!dataGenerationAction.equalsIgnoreCase("add") && !dataGenerationAction.equalsIgnoreCase("remove")){
            log.error("Data generation action only supports the values 'ADD' to create data or 'REMOVE' to remove the data");
            throw new ExceptionInInitializerError("Data generation action only supports the values 'ADD' to create data or 'REMOVE' to remove the data");
        }
    }

    public static String getDataAction(){

        return System.getProperty(DATA_ACTION);

    }

    public static void initialisationErrorLog(ExceptionInInitializerError e){
        log.error("Data generation could not be initialized due to an unrecoverable error");
        log.error(e.getMessage());
    }


}
