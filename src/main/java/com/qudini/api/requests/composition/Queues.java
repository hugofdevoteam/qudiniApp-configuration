package com.qudini.api.requests.composition;

import com.qudini.api.RequestSender;
import com.qudini.api.requests.forms.QueueDetails;
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
import static com.qudini.api.rest.endpoints.QueueEndpoints.CHANGE_QUEUE_DETAILS;


@Slf4j
public class Queues {

    private RequestSender requestSender;

    private QudiniAppResponseDataUtils qudiniAppResponseDataUtils;
    private QueueDetails queueDetails;

    private static final String QUEUES_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String QUEUES_CSV_HEADER_VENUE_NAME = "venueName";
    private static final String QUEUES_CSV_HEADER_QUEUE_NAME = "name"; //queueName
    private static final String QUEUES_CSV_HEADER_AVG_SERVE_TIME = "averageServeTime";

    private static final String VENUE_ID = "venueId";

    private static final String DEFAULT_QUEUE_FILE_PATH = "src/main/resources/data/queues.csv";

    public Queues(RequestSender requestSender) {
        this.requestSender = requestSender;
        this.qudiniAppResponseDataUtils = new QudiniAppResponseDataUtils(requestSender);
        this.queueDetails = new QueueDetails();
    }


    public void createQueues()
            throws IOException {

        createQueues(DEFAULT_QUEUE_FILE_PATH);

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

    public void enableBookingWithDefaultQueuesDetailsUsingCSV() throws IOException {

        try (
                Reader reader = Files.newBufferedReader(Paths.get(DEFAULT_QUEUE_FILE_PATH));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            for (CSVRecord csvRecord : csvParser) {

                enableBookingWithDefaultQueueDetails(
                        csvRecord.get(QUEUES_CSV_HEADER_MERCHANT_NAME),
                        csvRecord.get(QUEUES_CSV_HEADER_VENUE_NAME),
                        csvRecord.get(QUEUES_CSV_HEADER_QUEUE_NAME),
                        csvRecord.get(QUEUES_CSV_HEADER_AVG_SERVE_TIME));

            }
        } catch (IOException e) {
            log.error(String.format("There was a problem with the stated csv file or filepath: %s", DEFAULT_QUEUE_FILE_PATH));
            throw e;
        }
    }

    public void enableBookingWithDefaultQueueDetails(
            String merchantName,
            String venueName,
            String queueName,
            String averageServeTime
    ) throws UnsupportedEncodingException {

        log.debug(String.format("Obtaining venue id from venue name [%s]", venueName));

        String venueId = qudiniAppResponseDataUtils.getVenueIdByName(merchantName, venueName);

        log.debug("Obtaining queue id and identifier");

        List<String> queueIdentifications = qudiniAppResponseDataUtils.getQueueIdentifications(venueId, queueName);
        String queueId = queueIdentifications.get(0);
        String queueIdentification = queueIdentifications.get(1);

        log.debug("Obtaining queue SNS Topic identification");

        String snsTopicIdentification = qudiniAppResponseDataUtils.getTopicIdFromQueueDetails(queueIdentification);

        List<NameValuePair> formPropertiesForEnableBooking = queueDetails.defaultPropertiesForQueueDetailsWithBookingEnabled(
                queueId,
                queueIdentification,
                queueName,
                averageServeTime,
                snsTopicIdentification);

        log.info(String.format("Calling the endpoint uri [ %s ] to activate the queue for booking in the queue details", CHANGE_QUEUE_DETAILS));

        String response = requestSender.sendPut(CHANGE_QUEUE_DETAILS, formPropertiesForEnableBooking, "UTF-8");

        log.debug(String.format("After changing the queue details obtain the response : %n%s", response));

    }


}
