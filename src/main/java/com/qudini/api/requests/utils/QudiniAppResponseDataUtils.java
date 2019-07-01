package com.qudini.api.requests.utils;

import com.jayway.jsonpath.JsonPath;
import com.qudini.api.RequestSender;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.qudini.api.rest.endpoints.MerchantEndpoints.GET_ALL_MERCHANTS_FOR_CONTACT;
import static com.qudini.api.rest.endpoints.ProductsEndpoints.GET_PRODUCTS_FOR_MERCHANT_ID;
import static com.qudini.api.rest.endpoints.QueueEndpoints.*;
import static com.qudini.api.rest.endpoints.VenueEndpoints.GET_VENUE_FOR_MERCHANT_ID;
import static com.qudini.api.rest.json.paths.MerchantPaths.MERCHANT_ID_WITH_NAME;
import static com.qudini.api.rest.json.paths.ProductPaths.PRODUCT_AVG_SERVE_TIME_MINUTES_FOR_PRODUCT_NAME;
import static com.qudini.api.rest.json.paths.ProductPaths.PRODUCT_ID_WITH_NAME;
import static com.qudini.api.rest.json.paths.QueuePaths.*;
import static com.qudini.api.rest.json.paths.VenuePaths.VENUES_IDS_FOR_MERCHANT_ID;
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

        String endpointUri = String.format(GET_VENUE_FOR_MERCHANT_ID, merchantId);

        log.info(String.format("Calling the endpoint uri [ %s ] to obtain the venue id for venue with name [ %s ]", endpointUri, venueName));

        String venueResponse = requestSender.sendGet(endpointUri);

        log.debug(String.format("Fetching venue Id for the venue name: %s", venueName));

        List<Integer> ids = JsonPath.read(venueResponse, String.format(VENUE_ID_WITH_NAME, venueName));

        log.info(String.format("Found the id [ %s ] for the venue with the name [ %s ]", ids.get(0).toString(), venueName));

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

    /**
     * Method that returns the product Id associated to a merchant (id) taking into account the product name
     *
     * @param merchantName the name of the merchant
     * @param productName the name of the product
     * @return the product id
     */
    public String getProductIdByProductNameForMerchantId(String merchantName, String productName){

        String merchantId = getMerchantId(merchantName);

        String getProductsForMerchantId = requestSender.sendGet(String.format(GET_PRODUCTS_FOR_MERCHANT_ID, merchantId));

        return getProductIdByProductName(getProductsForMerchantId, productName);

    }

    /**
     * Method that returns the previously configured average serve time in minutes for the product associated with the merchant
     * taking into account the product name
     *
     * @param merchantName the name of the merchant
     * @param productName the name of the product
     * @return the previously configured serve time in minutes for the product
     */
    public String getProductAverageServeTimeByProductNameForMerchantId(String merchantName, String productName){

        String merchantId = getMerchantId(merchantName);

        String getProductsForMerchantId = requestSender.sendGet(String.format(GET_PRODUCTS_FOR_MERCHANT_ID, merchantId));

        return getProductServeTimeInMinutesByProductName(getProductsForMerchantId, productName);

    }

    /**
     * Method that returns both the queue id and the queue identifier in a list
     *
     * @return returns list with queue id and queue identifier
     */
    public List<String> getQueueIdentifications(String venueId, String queueName){

        log.debug(String.format("Getting queue identifications for queue with name: %s", queueName));

        String endpointUri = String.format(LIST_QUEUES_ADMIN_DATA,venueId);

        log.info(String.format("Calling the endpoint uri [ %s ] to obtain the queue [ %s ] identifications", endpointUri, queueName));

        String response = requestSender.sendGet(endpointUri);

        log.debug(String.format("The response obtain after calling [ %s ] is: %n%s", endpointUri, response));

        List<String> queueIdentifications = extractQueueIdentifications(response, queueName);

        log.info(String.format("Found the id [ %s ] and identifier [ %s ] for the queue [ %s ]",
                queueIdentifications.get(0),
                queueIdentifications.get(1),
                queueName));

        return queueIdentifications;


    }

    public String getQueueDetails(String queueIdentifier){

        log.info(String.format("Getting queue detail for queue with identifier [ %s ]", queueIdentifier));

        String endpointUri = String.format(LIST_QUEUE_DETAILS, queueIdentifier);

        log.info(String.format("Calling the endpoint uri [ %s ] to obtain the queue details", endpointUri));

        String response = requestSender.sendGet(endpointUri);

        log.debug(String.format("The response obtain after calling [ %s ] is: %n%s", endpointUri, response));

        return response;

    }

    public String getTopicIdFromQueueDetails(String queueIdentifier){

        String response = getQueueDetails(queueIdentifier);

        String snsTopicId = JsonPath.read(response, QUEUE_DETAILS_SNS_TOPIC_VALUE);

        log.info(String.format("Obtain the SNS Topic identified with [ %s ]", snsTopicId));

        return snsTopicId;

    }

    public List<Integer> getVenuesIdsForMerchantId(String merchantId){

        String endpointUri = String.format(GET_VENUE_FOR_MERCHANT_ID, merchantId);

        log.info(String.format("Fetching the venues information for the merchant with the id [ %s ] using the endpoint [ %s ]", merchantId, endpointUri));

        String response = requestSender.sendGet(endpointUri);

        log.debug(String.format("Obtain the following venues information: %n%s", response));

        log.info(String.format("Extracting a list of venues ids for the merchant with id [ %s ]", merchantId));

        List<Integer> venuesIds = JsonPath.read(response, VENUES_IDS_FOR_MERCHANT_ID);

        log.info(String.format("Obtained the list venues ids [ %s ] for merchant with id [ %s ]", venuesIds.toString(), merchantId));

        return venuesIds;

    }

    /**
     * Returns the full information of queues linked to each venue
     *
     * @param venueId the venue Id
     * @return full response
     */
    public String getQueueInfoPerVenueId(String venueId){

        String endpoint = String.format(QUEUES_INFO_FOR_EACH_VENUE, venueId);

        log.info(String.format("Getting queues info for venue Id [ %s] using the endpoint [ %s ]", venueId, endpoint));

        String response = requestSender.sendGet(endpoint);

        log.debug(String.format("Obtained the following queues info response for venue with id [ %s ]: %n%s", venueId, response));

        return response;
    }


    /**
     * Method that returns a list of queue names that it may be found in the response provided by
     * {@link #getQueueInfoPerVenueId(String) getQueueInfoPerVenueId}
     *
     * @return List of queue names
     */
    public List<String> extractQueueNamesOnResponse(String queueInfoPerVenueIdResponse){

        log.debug("Extracting the queue names from the queue info per venue response");

        return JsonPath.read(queueInfoPerVenueIdResponse, RETRIEVE_QUEUES_NAMES_FOR_VENUE_QUEUES);

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

        log.debug(String.format("Getting product Id for product name: %s", productName));

        List<Integer> ids = JsonPath.read(productsForMerchantIdResponse, String.format(PRODUCT_ID_WITH_NAME, productName));

        log.info(String.format("Found the product id [ %s ] matching the product name [ %s ]", ids.toString(), productName));

        return ids.get(0).toString();

    }

    private String getProductServeTimeInMinutesByProductName(
            String productsForMerchantIdResponse,
            String productName) {

        log.debug(String.format("Getting product average serve time in minutes for product name: %s", productName));

        List<Integer> listOfAvgServeTimeInMinutes = JsonPath.read(productsForMerchantIdResponse, String.format(PRODUCT_AVG_SERVE_TIME_MINUTES_FOR_PRODUCT_NAME, productName));

        log.info(String.format("Found the product serve time in minutes [ %s ] matching the product name [ %s ]", listOfAvgServeTimeInMinutes.toString(), productName));

        return listOfAvgServeTimeInMinutes.get(0).toString();

    }

    private List<String> extractQueueIdentifications(String adminQueueResponse, String queueName){

        List<String> queueIdentifications = new ArrayList<>();

        List<Integer> listIds = JsonPath.read(adminQueueResponse, String.format(RETRIEVE_QUEUE_DETAILS_QUEUE_ID_FOR_QUEUE_WITH_NAME, queueName));

        String id = String.valueOf(listIds.get(0));

        log.debug(String.format("Extracting queue Id from the response, found Id: %s", id));

        List<String> listIdentifiers = JsonPath.read(adminQueueResponse, String.format(RETRIEVE_QUEUE_DETAILS_QUEUE_IDENTIFIER_FOR_QUEUE_WITH_NAME, queueName));

        String identifier = listIdentifiers.get(0);

        log.debug(String.format("Extracting queue Identifier from the response, found Identifier: %s", identifier));

        queueIdentifications.add(id);
        queueIdentifications.add(identifier);

        return queueIdentifications;
    }
}
