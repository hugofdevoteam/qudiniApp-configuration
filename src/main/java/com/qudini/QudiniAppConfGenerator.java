package com.qudini;

import lombok.extern.slf4j.Slf4j;

import static com.qudini.configuration.GlobalConfiguration.configuration;

@Slf4j
public class QudiniAppConfGenerator {

    public static void main(String args[]) {

        String appUrl = configuration.getQudiniAppStaticData().getBaseuri();
        String admin_username = configuration.getQudiniAppStaticData().getUser();
        String admin_password = configuration.getQudiniAppStaticData().getPassword();

        log.error("QudiniApp Base URL: " + appUrl + "QudiniApp User: " + admin_username + "QudiniApp Password: " + admin_password);

    }
}
