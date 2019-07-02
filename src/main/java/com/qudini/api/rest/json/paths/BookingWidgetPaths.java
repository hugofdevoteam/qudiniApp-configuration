package com.qudini.api.rest.json.paths;

public final class BookingWidgetPaths {

    private BookingWidgetPaths(){}

    public static final String GET_BOOKING_WIDGET_ID_FROM_BW_TITLE = "$.[?(@.title=='%s')].id";
}
