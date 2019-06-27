package com.qudini.api.requests.composition;

import com.jayway.jsonpath.JsonPath;
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
import java.util.List;

import static com.qudini.api.requests.composition.Merchants.getMerchantIdByName;
import static com.qudini.api.rest.endpoints.MerchantEndpoints.GET_ALL_MERCHANTS_FOR_CONTACT;
import static com.qudini.api.rest.endpoints.VenueEndpoints.ADD_MERCHANT_VENUE;
import static com.qudini.api.rest.endpoints.VenueEndpoints.GET_VENUE_FOR_MERCHANT_ID;
import static com.qudini.api.rest.json.paths.VenuePaths.VENUE_ID_WITH_NAME;


@Slf4j
public class Venues {

    private RequestSender requestSender;

    private static final String VENUE_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String VENUE_CSV_HEADER_VENUE_NAME = "venueName";

    public Venues(RequestSender requestSender) {
        this.requestSender = requestSender;
    }

    public void createVenues()
            throws IOException {

        createVenues("src/main/resources/data/venues.csv");

    }

    public void createVenues(
            String venuesFilePath)
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
                        csvRecord.get(VENUE_CSV_HEADER_VENUE_NAME));

            }
        } catch (IOException e) {
            log.error(String.format("There was a problem with the stated csv file or filepath: %s", venuesFilePath));
            throw e;
        }

    }

    public void createVenue(
            String merchantName,
            String venueName)
            throws UnsupportedEncodingException {


        String merchantId = getMerchantId(merchantName);

        if (!merchantId.equals("")) {

            log.info(String.format("Found the id [ %s ] for the merchant with name [ %s ]", merchantId, merchantName));

            String resourceUri = String.format(ADD_MERCHANT_VENUE, merchantId, venueName);

            log.info(String
                    .format("Creating venue with name [ %s ] for merchant [%s - %s] using the resource URI [ %s ]",
                            venueName,
                            merchantId,
                            merchantName,
                            resourceUri));

            String response = requestSender.sendPost(resourceUri, "application/json");

            log.debug(String.format("Response for create venue: %s", response));

        } else {

            log.error(String.format("Could find an id for the merchant with name [ %s ]", merchantName));
        }


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


    // PRIVATE METHODS

    private String getMerchantId(String merchantName) {

        log.info(String.format("Request information for all the contact merchants using the endpoint URI: %s", GET_ALL_MERCHANTS_FOR_CONTACT));

        String getMerchantsResponse = requestSender.sendGet(GET_ALL_MERCHANTS_FOR_CONTACT);

        log.debug(String.format("Fetching merchant Id for the merchant name: %s", merchantName));

        return getMerchantIdByName(getMerchantsResponse, merchantName);

    }
}
