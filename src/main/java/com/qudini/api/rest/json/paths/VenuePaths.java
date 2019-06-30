package com.qudini.api.rest.json.paths;

public final class VenuePaths {

    private VenuePaths() {
    }

    public static final String VENUE_ID_WITH_NAME = "$.[?(@.name=='%s')].id";

    public static final String VENUES_IDS_FOR_MERCHANT_ID = "$.[*].id";
}
