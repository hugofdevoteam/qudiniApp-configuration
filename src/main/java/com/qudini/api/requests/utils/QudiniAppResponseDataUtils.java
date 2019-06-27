package com.qudini.api.requests.utils;

import com.jayway.jsonpath.JsonPath;
import com.qudini.api.RequestSender;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.qudini.api.rest.endpoints.MerchantEndpoints.GET_ALL_MERCHANTS_FOR_CONTACT;
import static com.qudini.api.rest.endpoints.Products.GET_PRODUCTS_FOR_MERCHANT_ID;
import static com.qudini.api.rest.endpoints.VenueEndpoints.GET_VENUE_FOR_MERCHANT_ID;
import static com.qudini.api.rest.json.paths.MerchantPaths.MERCHANT_ID_WITH_NAME;
import static com.qudini.api.rest.json.paths.ProductPaths.PRODUCT_ID_WITH_NAME;
import static com.qudini.api.rest.json.paths.VenuePaths.VENUE_ID_WITH_NAME;

@Slf4j
public class QudiniAppResponseDataUtils {

    private RequestSender requestSender;

    public QudiniAppResponseDataUtils(RequestSender requestSender){
        this.requestSender = requestSender;
    }


    public String getVenueIdByName(
            String merchantName,
            String venueName) {

        String merchantId = getMerchantId(merchantName);

        String venueResponse = requestSender.sendGet(String.format(GET_VENUE_FOR_MERCHANT_ID, merchantId));

        log.debug(String.format("Fetching venue Id for the venue name: %s", venueName));

        List<Integer> ids = JsonPath.read(venueResponse, String.format(VENUE_ID_WITH_NAME, venueName));

        log.info(String.format("Found the id %s for the venue with the name [ %s ]", ids.get(0).toString(), venueName));

        return ids.get(0).toString();

    }

    public String getMerchantId(String merchantName) {

        log.info(String.format("Request information for all the contact merchants using the endpoint URI: %s", GET_ALL_MERCHANTS_FOR_CONTACT));

        log.debug(String.format("Fetching merchant Id for the merchant name: %s", merchantName));

        String getMerchantsResponse = requestSender.sendGet(GET_ALL_MERCHANTS_FOR_CONTACT);

        String merchantId = getMerchantIdByName(getMerchantsResponse, merchantName);

        log.debug(String.format("Found merchant with Id [ %s ] for the merchant with name [ %s ]", merchantId, merchantName));

        return merchantId;

    }

    public String getProductIdByProductNameForMerchantId(String merchantName, String productName){

        String merchantId = getMerchantId(merchantName);

        String getProductsForMerchantId = requestSender.sendGet(String.format(GET_PRODUCTS_FOR_MERCHANT_ID, merchantId));

        return getProductIdByProductName(getProductsForMerchantId, productName);

    }


    //PRIVATE METHODS

    private String getMerchantIdByName(
            String merchantResponse,
            String merchantName) {

        List<Integer> ids = JsonPath.read(merchantResponse, String.format(MERCHANT_ID_WITH_NAME, merchantName));

        return ids.get(0).toString();

    }

    private String getProductIdByProductName(
            String productsForMerchantIdResponse,
            String productName) {

        log.info(String.format("Getting product Id for product name: %s", productName));

        List<Integer> ids = JsonPath.read(productsForMerchantIdResponse, String.format(PRODUCT_ID_WITH_NAME, productName));

        log.info(String.format("Found the product id %s matching the product name [ %s ]", ids.toString(), productName));

        return ids.get(0).toString();

    }
}
