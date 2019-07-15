package com.qudini.api.rest.endpoints;

public final class ProductsEndpoints {

    private ProductsEndpoints() {
    }

    public static final String ADD_OR_REMOVE_PRODUCT_QUEUES = "/api/merchant/product";

    public static final String GET_PRODUCTS_FOR_MERCHANT_ID = "/api/merchant/product?merchantId=%s";
}
