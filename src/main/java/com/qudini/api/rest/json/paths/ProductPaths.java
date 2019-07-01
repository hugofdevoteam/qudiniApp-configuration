package com.qudini.api.rest.json.paths;

public final class ProductPaths {

    private ProductPaths() {
    }

    public static final String PRODUCT_ID_WITH_NAME = "$.[?(@.productName=='%s')].id";

    public static final String PRODUCT_AVG_SERVE_TIME_MINUTES_FOR_PRODUCT_NAME = "$.[?(@.productName=='%s')].averageServeTimeMinutes";
}
