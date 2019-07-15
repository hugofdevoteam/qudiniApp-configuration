package com.qudini.generators;

import com.qudini.api.RequestSender;
import com.qudini.api.requests.composition.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BookingWigetCreator {

    private String appsBaseUri;
    private String qudiniAppUsername;
    private String qudiniAppPassword;

    public BookingWigetCreator(
            String appsBaseUri,
            String username,
            String password) {
        this.appsBaseUri = appsBaseUri;
        this.qudiniAppUsername = username;
        this.qudiniAppPassword = password;

    }

    public void createMinimalBookingWidget() {

        RequestSender requestSender = new RequestSender(appsBaseUri, qudiniAppUsername, qudiniAppPassword);

        Merchants merchants = new Merchants(requestSender);
        Venues venues = new Venues(requestSender);
        Queues queues = new Queues(requestSender);
        Products products = new Products(requestSender);
        BookingWidget bookingWidget = new BookingWidget(requestSender, appsBaseUri);

        try {
            merchants.createMerchants();
            venues.createVenues();
            queues.createQueues();
            queues.enableBookingWithDefaultQueuesDetailsUsingCSV();
            products.createProductsAssociatedToQueues();
            bookingWidget.createMinimalBookingWidget();

        } catch (IOException e) {
            log.error("There was a I/O problem when performing the data configuration on QudiniApp");
            log.error(e.getMessage());
        }
    }
}
