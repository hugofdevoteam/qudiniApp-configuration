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

@Slf4j
public class Merchants extends RequestSender{

    private static final String MERCHANT_CSV_HEADER_NAME = "Name";
    private static final String MERCHANT_CSV_HEADER_MAX_VENUES = "maxVenues";
    private static final String MERCHANT_CSV_HEADER_TEMPLATE_KEY = "templateKey";
    private static final String MERCHANT_CSV_HEADER_CONTRACT_STATUS_KEY = "contractStatusKey";
    private static final String MERCHANT_CSV_HEADER_INDUSTRY_SECTOR_KEY = "industrySectorKey";
    private static final String MERCHANT_CSV_HEADER_SIZE_CATEGORY_KEY = "sizeCategoryKey";
    private static final String MERCAHNT_CSV_HEADER_SALES_REGION_KEY = "salesRegionKey";
    private static final String MERCHANT_CSV_HEADER_COUNTRY_KEY = "countryKey";
    private static final String MERCHANT_CSV_HEADER_TIMEZONE_KEY = "timezoneKey";
    private static final String MERCHANT_CSV_HEADER_LANGUAGE_KEY = "languageKey";
    private static final String MERCHANT_CSV_HEADER_BILLING_TYPE_KEY = "billingTypeKey";
    private static final String MERCHANT_CSV_HEADER_SALES_ASSIGNEE_KEY = "salesAssigneeKey";
    private static final String MERCHANT_CSV_HEADER_QUESTIONS_LIMIT_COUNT = "questionsLimitCount";


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
                        csvRecord.get(MERCAHNT_CSV_HEADER_SALES_REGION_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_COUNTRY_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_TIMEZONE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_LANGUAGE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_BILLING_TYPE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_SALES_ASSIGNEE_KEY),
                        csvRecord.get(MERCHANT_CSV_HEADER_QUESTIONS_LIMIT_COUNT),
                        envBaseUri,
                        qudiniAppUsername,
                        qudiniAppPassword);

            }
        } catch (IOException e) {
            log.error(String.format("There was a problem with the stated csv file or filepath: %s", merchantsFilePath));
            throw e;
        }


    }

    public void createMerchants(
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws IOException {

        createMerchants("src/main/resources/data/merchants.csv", envBaseUri, qudiniAppUsername, qudiniAppPassword);
        
    }

    public void createMerchant(List<NameValuePair> merchantNameValuePairs,
                               String envBaseUri,
                               String qudiniAppUsername,
                               String qudiniAppPassword) throws UnsupportedEncodingException {

        String url = String.format("%s/api-merchant-add", envBaseUri);

        String response = sendPost(
                url,
                generateQudiniAppToken(qudiniAppUsername, qudiniAppPassword),
                merchantNameValuePairs,
                "UTF-8");

        log.debug(String.format("Response: %s", response));

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
            String questionsLimitCount,
            String envBaseUri,
            String qudiniAppUsername,
            String qudiniAppPassword)
            throws UnsupportedEncodingException {

        List<NameValuePair> paramsAsNameValuePairList = new ArrayList<>();

        paramsAsNameValuePairList.add(new BasicNameValuePair("name", name));
        paramsAsNameValuePairList.add(new BasicNameValuePair("maxVenues", maxVenues));
        paramsAsNameValuePairList.add(new BasicNameValuePair("templateKey", templateKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("contractStatusKey", contractStatusKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("industrySectorKey", industrySectorKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("sizeCategoryKey", sizeCategoryKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("salesRegionKey", salesRegionKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("countryKey", countryKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("timezoneKey", timezoneKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("languageKey", languageKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("billingTypeKey", billingTypeKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("salesAssigneeKey", salesAssigneeKey));
        paramsAsNameValuePairList.add(new BasicNameValuePair("questionsLimitCount", questionsLimitCount));

        log.info(String.format("Trying to create a merchant with the following info: %s",
                paramsAsNameValuePairList
                        .stream()
                        .map(NameValuePair::getValue)
                        .collect(Collectors.toList())
                        .toString()));

        createMerchant(paramsAsNameValuePairList, envBaseUri, qudiniAppUsername, qudiniAppPassword);

    }

}
