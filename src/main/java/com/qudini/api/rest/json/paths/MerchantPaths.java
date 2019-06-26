package com.qudini.api.rest.json.paths;

public final class MerchantPaths {

    private MerchantPaths(){}

    public static final String MERCHANT_ID_WITH_NAME = "$.merchants[?(@.name=='%s')].id";

}
