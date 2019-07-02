package com.qudini;

import com.qudini.generators.BookingWidgetRemoval;
import com.qudini.generators.BookingWigetCreator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class QudiniAppDataGenerator {


    public static void main(String args[]) throws IOException {

        BookingWigetCreator bookingwigetCreator = new BookingWigetCreator();
        bookingwigetCreator.createMinimalBookingWidget();

        //BookingWidgetRemoval bookingWidgetRemoval = new BookingWidgetRemoval();
        //bookingWidgetRemoval.deleteMinimalBookingWidget();

    }
}
