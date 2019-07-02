package com.qudini.api.rest.endpoints;

public final class BookingWidgetEndpoints {

    private BookingWidgetEndpoints() {
    }

    public static final String ADD_BOOKING_WIDGET_FOR_MERCHANT = "/api/merchant/%s/bookingWidget";

    public static final String GET_BOOKING_WIDGETS_FOR_MERCHANT = "/api/merchant/%s/bookingWidgets";

    public static final String DELETE_BOOKING_WIDGET = "/api/bookingWidget";

}
