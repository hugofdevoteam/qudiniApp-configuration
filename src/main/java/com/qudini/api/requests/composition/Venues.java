package com.qudini.api.requests.composition;

import com.qudini.api.RequestSender;
import com.qudini.api.requests.utils.QudiniAppResponseDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.qudini.api.rest.endpoints.VenueEndpoints.ADD_MERCHANT_VENUE;


@Slf4j
public class Venues {

    private RequestSender requestSender;
    private QudiniAppResponseDataUtils qudiniAppResponseDataUtils;

    private static final String VENUE_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String VENUE_CSV_HEADER_VENUE_NAME = "venueName";

    public Venues(RequestSender requestSender) {
        this.requestSender = requestSender;
        this.qudiniAppResponseDataUtils = new QudiniAppResponseDataUtils(requestSender);
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


        String merchantId = qudiniAppResponseDataUtils.getMerchantId(merchantName);

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



    // PRIVATE METHODS


}
