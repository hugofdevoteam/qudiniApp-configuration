package com.qudini;

import com.qudini.generators.BookingwigetCreator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class QudiniAppDataGenerator {


    public static void main(String args[]) throws IOException {

        BookingwigetCreator bookingwigetCreator = new BookingwigetCreator();
        bookingwigetCreator.createMinimalBookingWidget();

    }
}
