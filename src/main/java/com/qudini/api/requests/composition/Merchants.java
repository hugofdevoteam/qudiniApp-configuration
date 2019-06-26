package com.qudini.api.requests.composition;

import com.jayway.jsonpath.JsonPath;
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

import static com.qudini.api.rest.endpoints.MerchantEndpoints.*;
import static com.qudini.api.rest.json.paths.MerchantPaths.MERCHANT_ID_WITH_NAME;

@Slf4j
public class Merchants extends RequestSender {

    private static final String MERCHANT_CSV_HEADER_NAME = "name";
    private static final String MERCHANT_CSV_HEADER_MAX_VENUES = "maxVenues";
    private static final String MERCHANT_CSV_HEADER_TEMPLATE_KEY = "templateKey";
    private static final String MERCHANT_CSV_HEADER_CONTRACT_STATUS_KEY = "contractStatusKey";
    private static final String MERCHANT_CSV_HEADER_INDUSTRY_SECTOR_KEY = "industrySectorKey";
    private static final String MERCHANT_CSV_HEADER_SIZE_CATEGORY_KEY = "sizeCategoryKey";
    private static final String MERCHANT_CSV_HEADER_SALES_REGION_KEY = "salesRegionKey";
    private static final String MERCHANT_CSV_HEADER_COUNTRY_KEY = "countryKey";
    private static final String MERCHANT_CSV_HEADER_TIMEZONE_KEY = "timezoneKey";
    private static final String MERCHANT_CSV_HEADER_LANGUAGE_KEY = "languageKey";
    private static final String MERCHANT_CSV_HEADER_BILLING_TYPE_KEY = "billingTypeKey";
    private static final String MERCHANT_CSV_HEADER_SALES_ASSIGNEE_KEY = "salesAssigneeKey";
    private static final String MERCHANT_CSV_HEADER_REPORT_WALKOUT_THRESHOLD = "reportWalkoutThreshold";


    public void createMerchants(
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws IOException {

        createMerchants("src/main/resources/data/merchants.csv", envBaseUri, qudiniAppUsername, qudiniAppPassword);

    }

    public void createMerchants(
            String merchantsFilePath,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws IOException {

        try (
                Reader reader = Files.newBufferedReader(Paths.get(merchantsFilePath));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            for (CSVRecord csvRecord : csvParser) {

                createMerchant(
                        csvRecord.get(MERCHANT_CSV_HEADER_NAME),
                        csvRecord.get(MERCHANT_CSV_HEADER_MAX_VENUES),
                        csvRecord.get(MERCHANT_CSV_HEADER_TEMPLATE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_CONTRACT_STATUS_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_INDUSTRY_SECTOR_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_SIZE_CATEGORY_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_SALES_REGION_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_COUNTRY_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_TIMEZONE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_LANGUAGE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_BILLING_TYPE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_SALES_ASSIGNEE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_REPORT_WALKOUT_THRESHOLD),
                        envBaseUri,
                        qudiniAppUsername,
                        qudiniAppPassword);

            }
        } catch (IOException e) {
            log.error(String.format("There was a problem with the stated csv file or filepath: %s", merchantsFilePath));
            throw e;
        }


    }

    public void createMerchant(
            String name,
            String maxVenues,
            String templateKey,
            String contractStatusKey,
            String industrySectorKey,
            String sizeCategoryKey,
            String salesRegionKey,
            String countryKey,
            String timezoneKey,
            String languageKey,
            String billingTypeKey,
            String salesAssigneeKey,
            String reportWalkoutThreshold,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws UnsupportedEncodingException {

        List<NameValuePair> paramsAsNameValuePairList = new ArrayList<>();

        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_NAME, name));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_MAX_VENUES, maxVenues));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_TEMPLATE_KEY, templateKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_CONTRACT_STATUS_KEY, contractStatusKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_INDUSTRY_SECTOR_KEY, industrySectorKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_SIZE_CATEGORY_KEY, sizeCategoryKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_SALES_REGION_KEY, salesRegionKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_COUNTRY_KEY, countryKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_TIMEZONE_KEY, timezoneKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_LANGUAGE_KEY, languageKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_BILLING_TYPE_KEY, billingTypeKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_SALES_ASSIGNEE_KEY, salesAssigneeKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair(MERCHANT_CSV_HEADER_REPORT_WALKOUT_THRESHOLD, reportWalkoutThreshold));


        createMerchant(paramsAsNameValuePairList, envBaseUri, qudiniAppUsername, qudiniAppPassword);

    }

    public void createMerchant(List<NameValuePair> merchantNameValuePairs,
                               String envBaseUri,
                               String qudiniAppUsername,
                               String qudiniAppPassword) throws UnsupportedEncodingException {

        String url = String.format("%s%s", envBaseUri, ADD_MERCHANT).trim();

        log.info(String.format("App is making a call to the url [%s] to create a merchant with the info: %s",
                url,
                merchantNameValuePairs
                        .stream()
                        .map(NameValuePair::getValue)
                        .collect(Collectors.toList())
                        .toString()));

        String response = sendPost(
                url,
                generateQudiniAppToken(qudiniAppUsername, qudiniAppPassword),
                merchantNameValuePairs,
                "UTF-8");

        log.debug(String.format("Obtained response from create merchant: %s", response));

    }


    public String getMerchantIdByName(
            String merchantResponse,
            String merchantName){

        List<Integer> ids = JsonPath.read(merchantResponse, String.format(MERCHANT_ID_WITH_NAME, merchantName));

        return ids.get(0).toString();

    }


}


