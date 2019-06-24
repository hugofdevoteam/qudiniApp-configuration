package com.qudini;

import com.qudini.api.requests.composition.Merchants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.qudini.configuration.GlobalConfiguration.*;

@Slf4j
public class QudiniAppDataGenerator {


    public static void main(String args[]) {

        Merchants merchants = new Merchants();

        String appBaseUrl = configuration.getQudiniAppStaticData().getBaseuri();
        String admin_username = configuration.getQudiniAppStaticData().getUser();
        String admin_password = configuration.getQudiniAppStaticData().getPassword();

        log.info(String.format("QudiniApp data generator started for environment: %s" , getEnv().toUpperCase()));
        log.error("QudiniApp Base URL: " + appBaseUrl + " QudiniApp User: " + admin_username + " QudiniApp Password: " + admin_password);

        try {
            merchants.createMerchants(appBaseUrl, admin_username, admin_password);
        }catch (IOException e) {
            e.printStackTrace();
        }



    }
}
