package com.qudini.api.rest.json.paths;

public final class VenuePaths {

    private VenuePaths() {
    }

    public static final String VENUE_ID_WITH_NAME = "$.[?(@.name=='%s')].id";
}
