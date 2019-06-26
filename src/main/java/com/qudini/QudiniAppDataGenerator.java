package com.qudini;

import com.qudini.api.requests.composition.Merchants;
import com.qudini.api.requests.composition.Queues;
import com.qudini.api.requests.composition.Venues;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.qudini.configuration.GlobalConfiguration.*;

@Slf4j
public class QudiniAppDataGenerator {


    public static void main(String args[]) throws IOException {

        Merchants merchants = new Merchants();
        Venues venues = new Venues();
        Queues queues = new Queues();

        String appBaseUrl = configuration.getQudiniAppStaticData().getBaseuri();
        String admin_username = configuration.getQudiniAppStaticData().getUser();
        String admin_password = configuration.getQudiniAppStaticData().getPassword();

        log.info(String.format("QudiniApp data generator started for environment: %s" , getEnv().toUpperCase()));
        log.info("QudiniApp Base URL: " + appBaseUrl + " QudiniApp User: " + admin_username); //" QudiniApp Password: " + admin_password

        try {
            merchants.createMerchants(appBaseUrl, admin_username, admin_password);
        }catch (IOException e) {
            e.printStackTrace();
        }

        venues.createVenues(appBaseUrl, admin_username, admin_password);
        queues.createQueues(appBaseUrl, admin_username, admin_password);



    }
}
