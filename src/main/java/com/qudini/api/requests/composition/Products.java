package com.qudini.api.requests.composition;

import com.jayway.jsonpath.JsonPath;
import com.qudini.api.RequestSender;
import com.qudini.api.requests.utils.QudiniAppResponseDataUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.qudini.api.rest.endpoints.Products.ADD_PRODUCT_QUEUES;
import static com.qudini.api.rest.endpoints.VenueEndpoints.GET_VENUE_FOR_MERCHANT_ID;
import static com.qudini.api.rest.json.paths.QueuePaths.*;
import static java.lang.Integer.parseInt;

@Slf4j
public class Products {

    private RequestSender requestSender;
    private QudiniAppResponseDataUtils qudiniAppResponseDataUtils;

    private static final String PRODUCT_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String PRODUCT_CSV_HEADER_PRODUCT_NAME = "productName";
    private static final String PRODUCT_CSV_HEADER_AVG_SERVE_TIME_MIN = "averageServeTimeMinutes";
    private static final String PRODUCT_CSV_HEADER_QUEUES = "queues"; //space separated queues

    private static final String MERCHANT_ID = "merchantId";
    private static final String TYPE = "type";

    public Products(RequestSender requestSender) {
        this.requestSender = requestSender;
        this.qudiniAppResponseDataUtils = new QudiniAppResponseDataUtils(requestSender);

    }

    // PUBLIC METHODS

    // CREATE PRODUCT

    public void createProductsAssociatedToQueues()
            throws IOException {

        createProductsAssociatedToQueues("src/main/resources/data/products.csv");


    }

    public void createProductsAssociatedToQueues(
            String productsFilePath)
            throws IOException {


        try (
                Reader reader = Files.newBufferedReader(Paths.get(productsFilePath));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ){
            for (CSVRecord csvRecord : csvParser) {

                createProductAssociatedToQueues(
                        csvRecord.get(PRODUCT_CSV_HEADER_MERCHANT_NAME),
                        csvRecord.get(PRODUCT_CSV_HEADER_PRODUCT_NAME),
                        csvRecord.get(PRODUCT_CSV_HEADER_AVG_SERVE_TIME_MIN),
                        Arrays.asList(csvRecord.get(PRODUCT_CSV_HEADER_QUEUES).split(" "))
                );
            }

        }catch (IOException e) {
                log.error(String.format("There was a problem accessing or reading the csv file or filepath: %s", productsFilePath));
                throw e;
            }


    }

    public void createProductAssociatedToQueues(
            String merchantName,
            String productName,
            String averageServeTimeMinutes,
            List<String> queues) {

        JSONObject jsonObject = createProductAssociatedToQueuesPayload(
                merchantName,
                productName,
                averageServeTimeMinutes,
                queues);

        createProductAssociatedToQueues(jsonObject.toJSONString());

    }

    public void createProductAssociatedToQueues(String paramsAsJsonString) {

        log.info(String.format("App is making a call to the resourceUri [%s] to create a product with the info: %s",
                ADD_PRODUCT_QUEUES, paramsAsJsonString));

        requestSender.sendPost(
                ADD_PRODUCT_QUEUES,
                "application/json",
                paramsAsJsonString,
                "UTF-8");

    }

    // ADD PRODUCT TO BOOKING, QUEUE OR BOTH


//    public void showProductFor(List<String> bookingFor) throws IOException {
//
//        showProductFor("src/main/resources/data/products.csv", bookingFor);
//
//    }
//
//    public void showProductFor(String productsFilePath, List<String> bookingFor) throws IOException {
//
//        try (
//                Reader reader = Files.newBufferedReader(Paths.get(productsFilePath));
//                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
//                        .withFirstRecordAsHeader()
//                        .withIgnoreHeaderCase()
//                        .withTrim())
//        ){
//            for (CSVRecord csvRecord : csvParser) {
//
//                showProductFor(
//                        csvRecord.get(PRODUCT_CSV_HEADER_MERCHANT_NAME),
//                        csvRecord.get(PRODUCT_CSV_HEADER_PRODUCT_NAME),
//                        csvRecord.get(PRODUCT_CSV_HEADER_AVG_SERVE_TIME_MIN),
//                        bookingFor
//                );
//            }
//
//        }catch (IOException e) {
//            log.error(String.format("There was a problem accessing or reading the csv file or filepath: %s", productsFilePath));
//            throw e;
//        }
//
//
//
//    }
//
//    public void showProductFor(
//            String merchantName,
//            String productName,
//            String averageServeTimeMinutes,
//            List<String> bookingFor){
//
//        JSONObject jsonPriceObj = new JSONObject();
//        jsonPriceObj.put("amount", null);
//        jsonPriceObj.put("currency", null);
//
//        Integer productId = parseInt(qudiniAppResponseDataUtils.getProductIdByProductNameForMerchantId(merchantName, productName));
//
//        JSONObject jsonObject = createProductAssociatedToQueuesPayload(merchantName, productName, averageServeTimeMinutes);
//
//        jsonObject.put("productId", productId);
//        jsonObject.put("bookingFor", bookingFor);
//        jsonObject.put("iconId", null);
//        jsonObject.put("maxAddons", null);
//        jsonObject.put("updateKiosk", false);
//        jsonObject.put("colour", null);
//        jsonObject.put("priority", 0);
//        jsonObject.put("description", null);
//        jsonObject.put("price", jsonPriceObj);
//
//        log.info(String.format("Performing a request to [ %s ] with the payload: %n%s ", ADD_PRODUCT_QUEUES, jsonObject.toJSONString()));
//
//        String response = requestSender.sendPost(
//                ADD_PRODUCT_QUEUES,
//                "application/json",
//                jsonObject.toJSONString(),
//                "UTF-8");
//
//        log.debug(String.format("Response obtained for activating a product for %s: %n%s",bookingFor.toString(), response));
//
//    }


    //PRIVATE METHODS
    private JSONObject createProductAssociatedToQueuesPayload(
            String merchantName,
            String productName,
            String averageServeTimeMinutes){

        Integer merchantId = parseInt(qudiniAppResponseDataUtils.getMerchantId(merchantName));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MERCHANT_ID, merchantId);
        jsonObject.put(PRODUCT_CSV_HEADER_PRODUCT_NAME, productName);
        jsonObject.put(PRODUCT_CSV_HEADER_AVG_SERVE_TIME_MIN, averageServeTimeMinutes);
        jsonObject.put(TYPE,"PRODUCT");

        return jsonObject;
    }


    private JSONObject createProductAssociatedToQueuesPayload(
            String merchantName,
            String productName,
            String averageServeTimeMinutes,
            List<String> queuesNames){

        JSONObject jsonObject = createProductAssociatedToQueuesPayload(
                merchantName,
                productName,
                averageServeTimeMinutes);
        jsonObject.put(PRODUCT_CSV_HEADER_QUEUES, createQueuesToProductArray(merchantName, queuesNames));

        return jsonObject;
    }

    private JSONArray createQueuesToProductArray(String merchantName, List<String> queuesNames){

        JSONArray queuesArray = new JSONArray();

        String merchantId = qudiniAppResponseDataUtils.getMerchantId(merchantName);

        List<Integer> venuesIds = qudiniAppResponseDataUtils.getVenuesIdsForMerchantId(merchantId);

        for (Integer venueId : venuesIds){

            String queueInfoResponseFromVenueId = qudiniAppResponseDataUtils.getQueueInfoPerVenueId(String.valueOf(venueId));

            List<String> queueNamesFromVenueId = qudiniAppResponseDataUtils.extractQueueNamesOnResponse(queueInfoResponseFromVenueId);

            for (String extractedQueueName: queueNamesFromVenueId){

                if(queuesNames.contains(extractedQueueName)){

                    log.info(String.format("List of queues [ %s ] contain the queue name [ %s ] the needed info will be added to the queue array to link it to the product",
                            queuesNames, extractedQueueName));

                    //MUST BE EXTRACTED FOR THE extractedQueueName
                    List<String> queuesIdentifier = JsonPath.read(queueInfoResponseFromVenueId, String.format(RETRIEVE_QUEUE_DETAILS_QUEUE_IDENTIFIER_FOR_QUEUE_WITH_NAME,extractedQueueName));
                    String queueIdentifier = queuesIdentifier.get(0);

                    List<Integer> queuesId = JsonPath.read(queueInfoResponseFromVenueId, String.format(RETRIEVE_QUEUE_DETAILS_QUEUE_ID_FOR_QUEUE_WITH_NAME, extractedQueueName));
                    Integer queueId = queuesId.get(0);

                    List<String> queuesVenueName = JsonPath.read(queueInfoResponseFromVenueId, String.format(RETRIEVE_VENUE_NAME_FROM_QUEUES_IN_VENUE_RESPONSE, extractedQueueName));
                    String queueVenueName = queuesVenueName.get(0);

                    JSONObject newQueueJSONObj = new JSONObject();
                    newQueueJSONObj.put("identifier", queueIdentifier);
                    newQueueJSONObj.put("name", extractedQueueName);
                    newQueueJSONObj.put("id", queueId);
                    newQueueJSONObj.put("venueId", venueId);
                    newQueueJSONObj.put("venueName", queueVenueName);
                    newQueueJSONObj.put("merchantId", merchantId);
                    newQueueJSONObj.put("merchantName", merchantName);
                    newQueueJSONObj.put("selected", true);

                    log.debug(String.format("Adding the following object to the Queues Array [ %s ]", newQueueJSONObj.toString()));

                    queuesArray.add(newQueueJSONObj);

                }

            }

        }
        log.debug(String.format("Returning the following Queues Array to link it to the product: %n%s", queuesArray.toString()));
        return queuesArray;

    }


}
