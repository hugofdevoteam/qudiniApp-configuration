package com.qudini;

import com.qudini.configuration.GlobalConfiguration;
import com.qudini.generators.BookingWidgetRemoval;
import com.qudini.generators.BookingWigetCreator;
import lombok.extern.slf4j.Slf4j;

import static com.qudini.configuration.GlobalConfiguration.configuration;

@Slf4j
public class QudiniAppDataGenerator {


    public static void main(String args[]) {


        if (GlobalConfiguration.getDataAction().equalsIgnoreCase("add")) {

            BookingWigetCreator bookingwigetCreator = new BookingWigetCreator(
                    configuration.getQudiniAppStaticData().getBaseuri(),
                    configuration.getQudiniAppStaticData().getUser(),
                    configuration.getQudiniAppStaticData().getPassword());

            bookingwigetCreator.createMinimalBookingWidget();
        } else if (GlobalConfiguration.getDataAction().equalsIgnoreCase("remove")) {

            BookingWidgetRemoval bookingWidgetRemoval = new BookingWidgetRemoval(
                    configuration.getQudiniAppStaticData().getBaseuri(),
                    configuration.getQudiniAppStaticData().getUser(),
                    configuration.getQudiniAppStaticData().getPassword());

            bookingWidgetRemoval.deleteMinimalBookingWidget();
        }

    }
}
