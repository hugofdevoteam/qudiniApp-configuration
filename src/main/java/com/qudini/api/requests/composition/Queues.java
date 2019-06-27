package com.qudini.api.requests.composition;

import com.qudini.api.RequestSender;
import com.qudini.api.requests.utils.QudiniAppResponseDataUtils;
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
public class Queues {

    private RequestSender requestSender;

    private QudiniAppResponseDataUtils qudiniAppResponseDataUtils;

    private static final String QUEUES_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String QUEUES_CSV_HEADER_VENUE_NAME = "venueName";
    private static final String QUEUES_CSV_HEADER_QUEUE_NAME = "name"; //queueName
    private static final String QUEUES_CSV_HEADER_AVG_SERVE_TIME = "averageServeTime";

    private static final String VENUE_ID = "venueId";

    public Queues(RequestSender requestSender) {
        this.requestSender = requestSender;
        this.qudiniAppResponseDataUtils = new QudiniAppResponseDataUtils(requestSender);
    }


    public void createQueues()
            throws IOException {

        createQueues("src/main/resources/data/queues.csv");

    }

    public void createQueues(
            String queuesFilePath)
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
                        csvRecord.get(QUEUES_CSV_HEADER_AVG_SERVE_TIME));

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
            String averageServeTime)
            throws UnsupportedEncodingException {

        String venueId = qudiniAppResponseDataUtils.getVenueIdByName(merchantName, venueName);

        List<NameValuePair> paramsAsNameValuePairList = new ArrayList<>();

        paramsAsNameValuePairList.add(new BasicNameValuePair(VENUE_ID, venueId));
        paramsAsNameValuePairList.add(new BasicNameValuePair(QUEUES_CSV_HEADER_QUEUE_NAME, queueName));
        paramsAsNameValuePairList.add(new BasicNameValuePair(QUEUES_CSV_HEADER_AVG_SERVE_TIME, averageServeTime));


        createQueue(paramsAsNameValuePairList);

    }

    public void createQueue(
            List<NameValuePair> queueNameValuePairs)
            throws UnsupportedEncodingException {


        log.info(String.format("App is making a call to the uri endpoint [%s] to create a queue with the info: %s",
                ADD_VENUE_QUEUE,
                queueNameValuePairs
                        .stream()
                        .map(NameValuePair::getValue)
                        .collect(Collectors.toList())
                        .toString()));

        String response = requestSender.sendPost(
                ADD_VENUE_QUEUE,
                queueNameValuePairs,
                "UTF-8");

        log.debug(String.format("Obtained response from create queue: %s", response));

    }


}
