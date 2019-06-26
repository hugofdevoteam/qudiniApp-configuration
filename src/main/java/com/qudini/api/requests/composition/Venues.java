package com.qudini.api.requests.composition;

import com.qudini.api.RequestSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.qudini.api.rest.endpoints.MerchantEndpoints.*;
import static com.qudini.api.rest.endpoints.VenueEndpoints.*;




@Slf4j
public class Venues extends RequestSender {

    private static final String VENUE_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String VENUE_CSV_HEADER_VENUE_NAME = "venueName";

    public void createVenues(
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws IOException {

        createVenues("src/main/resources/data/venues.csv", envBaseUri, qudiniAppUsername, qudiniAppPassword);



    }

    public void createVenues(
            String venuesFilePath,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws IOException {

        try (
                Reader reader = Files.newBufferedReader(Paths.get(venuesFilePath));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            for (CSVRecord csvRecord : csvParser) {

                createVenue(
                        csvRecord.get(VENUE_CSV_HEADER_MERCHANT_NAME),
                        csvRecord.get(VENUE_CSV_HEADER_VENUE_NAME),
                        envBaseUri,
                        qudiniAppUsername,
                        qudiniAppPassword);

            }
        } catch (IOException e) {
            log.error(String.format("There was a problem with the stated csv file or filepath: %s", venuesFilePath));
            throw e;
        }

    }

    public void createVenue(
            String merchantName,
            String venueName,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws UnsupportedEncodingException {


        String token = generateQudiniAppToken(qudiniAppUsername, qudiniAppPassword);

        String merchantId = getMerchantId(merchantName, envBaseUri, token);

        if (!merchantId.equals("")){

            log.info(String.format("Found the id [ %s ] for the merchant with name [ %s ]", merchantId, merchantName));

            String url = String
                    .format("%s%s",
                    envBaseUri,
                    String.format(ADD_MERCHANT_VENUE, merchantId, venueName));

            log.info(String.format("Creating venue with name [ %s ] for merchant [%s - %s] using the url [ %s ]", venueName, merchantId, merchantName, url));

            String response = sendPost(url,"application/json", token);

            log.debug(String.format("Response for create venue: %s", response));

        } else {

            log.error(String.format("Could find an id for the merchant with name [ %s ]", merchantName));
        }


    }


    // PRIVATE METHODS

    private String getMerchantId(String merchantName, String envBaseUri,  String token){

        Merchants merchants = new Merchants();

        //get merchant id using the merchant name
        String getMerchantsUrl = String.format("%s%s", envBaseUri, GET_ALL_MERCHANTS_FOR_CONTACT);

        log.info(String.format("Request information for all the contact merchants using the URL: %s", getMerchantsUrl));

        String getMerchantsResponse = sendGet(getMerchantsUrl, token);

        log.debug(String.format("Fetching merchant Id for the merchant name: %s", merchantName));

        return merchants.getMerchantIdByName(getMerchantsResponse, merchantName);

    }
}
