package com.qudini.api.requests.composition;

import com.qudini.api.RequestSender;
import com.qudini.api.requests.forms.BookingWidgetSetting;
import com.qudini.api.requests.utils.QudiniAppResponseDataUtils;
import lombok.extern.slf4j.Slf4j;
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

import static com.qudini.api.rest.endpoints.BookingWidgetEndpoints.ADD_BOOKING_WIDGET;

@Slf4j
public class BookingWidget {

    private RequestSender requestSender;
    private QudiniAppResponseDataUtils qudiniAppResponseDataUtils;
    private BookingWidgetSetting bookingWidgetSetting;
    private String bwBaseURI;

    private static final String BOOKING_WIDGET_DEFAULT_FILE_PATH = "src/main/resources/data/bookingwidgets.csv";

    private static final String BW_CSV_HEADER_MERCHANT_NAME = "merchantName";
    private static final String BW_CSV_HEADER_TITLE = "title";  //the title of the booking widget
    private static final String BW_CSV_HEADER_VENUES_NAMES = "venueNames";
    private static final String BW_CSV_HEADER_PRODUCTS_NAMES = "productsNames";

    public BookingWidget(RequestSender requestSender, String bwBaseUri) {

        this.requestSender = requestSender;
        this.bwBaseURI = bwBaseUri;
        this.qudiniAppResponseDataUtils = new QudiniAppResponseDataUtils(requestSender);
        this.bookingWidgetSetting = new BookingWidgetSetting(requestSender);

    }

    public void createMinimalBookingWidget() throws IOException {

        createMinimalBookingWidget(BOOKING_WIDGET_DEFAULT_FILE_PATH);

    }

    public void createMinimalBookingWidget(String bookingWidgetFilePath) throws IOException {

        try (
                Reader reader = Files.newBufferedReader(Paths.get(bookingWidgetFilePath));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            for (CSVRecord csvRecord : csvParser) {

                createMinimalBookingWidget(
                        csvRecord.get(BW_CSV_HEADER_TITLE),
                        bwBaseURI,
                        csvRecord.get(BW_CSV_HEADER_MERCHANT_NAME),
                        Arrays.asList(csvRecord.get(BW_CSV_HEADER_VENUES_NAMES).split(" ")),
                        Arrays.asList(csvRecord.get(BW_CSV_HEADER_PRODUCTS_NAMES).split(" "))
                );
            }

        } catch (IOException e) {
            log.error(String.format("There was a problem accessing or reading the csv file or filepath: %s", bookingWidgetFilePath));
            throw e;
        }

    }

    public void createMinimalBookingWidget(
            String title,
            String bwBaseUrl,
            String merchantName,
            List<String> venuesNames,  //Note that product and queues that belong to the venues should be linked previously
            List<String> productsNames) {

        String merchantId = qudiniAppResponseDataUtils.getMerchantId(merchantName);

        String endpoint = String.format(ADD_BOOKING_WIDGET, merchantId);

        JSONObject minimalConfBwPayload = bookingWidgetSetting.defaultPropertiesForBookingWidgetEnable(title, bwBaseUrl, merchantName, venuesNames, productsNames);

        log.info(String.format("App is making a call to the resourceUri [%s] to create a new Booking Widget",
                endpoint));

        String response = requestSender.sendPost(endpoint, "application/json", minimalConfBwPayload.toJSONString(), "UTF-8");

        log.debug(String.format("Obtained the following response after creating the booking widget: %n%s", response));

    }
}
