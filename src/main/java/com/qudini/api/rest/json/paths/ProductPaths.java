package com.qudini.api.rest.json.paths;

public final class ProductPaths {

    private ProductPaths() {
    }

    public static final String PRODUCT_ID_WITH_NAME = "$.[?(@.productName=='%s')].id";
}
