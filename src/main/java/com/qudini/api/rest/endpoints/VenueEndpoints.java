package com.qudini.api.rest.endpoints;

public final class VenueEndpoints {

    private VenueEndpoints() {
    }

    public static final String ADD_MERCHANT_VENUE = "/merchant-add-venue-submit?merchantId=%s&name=%s";

    public static final String GET_VENUE_FOR_MERCHANT_ID = "/api/merchant/venues?merchantId=%s";

}
