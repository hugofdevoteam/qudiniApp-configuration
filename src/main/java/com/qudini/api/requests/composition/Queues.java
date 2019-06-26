package com.qudini.api.requests.composition;

import com.qudini.api.RequestSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static com.qudini.api.rest.endpoints.QueueEndpoints.ADD_VENUE_QUEUE;


@Slf4j
public class Queues extends RequestSender {

    private static final String QUEUES_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String QUEUES_CSV_HEADER_VENUE_NAME = "venueName";
    private static final String QUEUES_CSV_HEADER_QUEUE_NAME = "name"; //queueName
    private static final String QUEUES_CSV_HEADER_AVG_SERVE_TIME = "averageServeTime";

    private static final String VENUE_ID = "venueId";

    private String token = "";


    public void createQueues(
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws IOException {

        createQueues("src/main/resources/data/queues.csv", envBaseUri, qudiniAppUsername, qudiniAppPassword);

    }

    public void createQueues(
            String queuesFilePath,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws IOException {

        try (
                Reader reader = Files.newBufferedReader(Paths.get(queuesFilePath));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            for (CSVRecord csvRecord : csvParser) {

                createQueue(
                        csvRecord.get(QUEUES_CSV_HEADER_MERCHANT_NAME),
                        csvRecord.get(QUEUES_CSV_HEADER_VENUE_NAME),
                        csvRecord.get(QUEUES_CSV_HEADER_QUEUE_NAME),
                        csvRecord.get(QUEUES_CSV_HEADER_AVG_SERVE_TIME),
                        envBaseUri,
                        qudiniAppUsername,
                        qudiniAppPassword);

            }
        } catch (IOException e) {
            log.error(String.format("There was a problem with the stated csv file or filepath: %s", queuesFilePath));
            throw e;
        }


    }

    public void createQueue(
            String merchantName,
            String venueName,
            String queueName,
            String averageServeTime,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws UnsupportedEncodingException {

        token = generateQudiniAppToken(qudiniAppUsername, qudiniAppPassword);

        String venueId = getVenueId(merchantName, venueName, envBaseUri, token);

        List<NameValuePair> paramsAsNameValuePairList = new ArrayList<>();

        paramsAsNameValuePairList.add(new BasicNameValuePair(VENUE_ID, venueId));
        paramsAsNameValuePairList.add(new BasicNameValuePair(QUEUES_CSV_HEADER_QUEUE_NAME, queueName));
        paramsAsNameValuePairList.add(new BasicNameValuePair(QUEUES_CSV_HEADER_AVG_SERVE_TIME, averageServeTime));


        createQueue(paramsAsNameValuePairList, envBaseUri, qudiniAppUsername, qudiniAppPassword);

    }

    public void createQueue(
            List<NameValuePair> queueNameValuePairs,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws UnsupportedEncodingException {

        String url = String.format("%s%s", envBaseUri, ADD_VENUE_QUEUE).trim();

        log.info(String.format("App is making a call to the url [%s] to create a queue with the info: %s",
                url,
                queueNameValuePairs
                        .stream()
                        .map(NameValuePair::getValue)
                        .collect(Collectors.toList())
                        .toString()));

        String response = sendPost(
                url,
                !token.equals("") ? token : generateQudiniAppToken(qudiniAppUsername, qudiniAppPassword),
                queueNameValuePairs,
                "UTF-8");

        log.debug(String.format("Obtained response from create merchant: %s", response));

    }

    //PRIVATE METHODS

    private String getVenueId(String merchantName, String venueName, String envBaseUri, String token){

        Venues venues = new Venues();

        return venues.getVenueIdByName(merchantName, venueName, envBaseUri, token);

    }


}
