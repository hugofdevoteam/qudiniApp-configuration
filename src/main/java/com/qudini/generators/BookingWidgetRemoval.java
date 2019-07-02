package com.qudini.generators;

import com.qudini.api.RequestSender;
import com.qudini.api.requests.composition.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.qudini.configuration.GlobalConfiguration.configuration;

@Slf4j
public class BookingWidgetRemoval {

    private static final String APPS_BASE_URI_FROM_CONFIG = configuration.getQudiniAppStaticData().getBaseuri();
    private static final String QUDINIAPP_USER_FROM_CONFIG = configuration.getQudiniAppStaticData().getUser();
    private static final String QUDINIAPP_PASSWORD_FROM_CONFIG = configuration.getQudiniAppStaticData().getPassword();

    public void deleteMinimalBookingWidget(){

        RequestSender requestSender = new RequestSender(APPS_BASE_URI_FROM_CONFIG, QUDINIAPP_USER_FROM_CONFIG, QUDINIAPP_PASSWORD_FROM_CONFIG);

        Merchants merchants = new Merchants(requestSender);
        Venues venues = new Venues(requestSender);
        Queues queues = new Queues(requestSender);
        Products products = new Products(requestSender);
        BookingWidget bookingWidget = new BookingWidget(requestSender, APPS_BASE_URI_FROM_CONFIG);

        try {

            bookingWidget.deleteBookingWidget();
            // missing products removal for merchant
            // missing queues removal for merchant
            // missing venues removal for merchant
            merchants.archiveMerchant();

        }catch (IOException e) {
            log.error("There was a I/O problem when performing the data configuration on QudiniApp");
            log.error(e.getMessage());
        }
    }
}
