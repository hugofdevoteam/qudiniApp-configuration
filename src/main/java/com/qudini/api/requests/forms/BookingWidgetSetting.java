package com.qudini.api.requests.forms;

import com.qudini.api.RequestSender;
import com.qudini.api.requests.utils.QudiniAppResponseDataUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.List;

import static java.lang.Integer.parseInt;

@Slf4j
public final class BookingWidgetSetting {

    private QudiniAppResponseDataUtils qudiniAppResponseDataUtils;

    public BookingWidgetSetting(RequestSender requestSender) {
        this.qudiniAppResponseDataUtils = new QudiniAppResponseDataUtils(requestSender);

    }

    public JSONObject defaultPropertiesForBookingWidgetEnable(
            String title,
            String bwBaseUrl,
            String merchantName,
            List<String> venuesNames,  //Note that product and queues that belong to the venues should be linked previously
            List<String> productsNames) {

        JSONObject bwEnablePayload = staticPropertiesForBookingWidgetEnable();
        bwEnablePayload.put("homeURL", bwBaseUrl);
        bwEnablePayload.put("title", title);
        bwEnablePayload.put("venues", populateVenuesArray(merchantName, venuesNames));
        bwEnablePayload.put("products", populateProductsArray(merchantName, productsNames));

        log.info(String.format("Created the following booking widget setup payload: %n%s", bwEnablePayload.toJSONString()));

        return bwEnablePayload;

    }

    public JSONObject staticPropertiesForBookingWidgetEnable() {

        log.debug("Fetching static json data for Booking Widget setup");

        Long epochTime = System.currentTimeMillis();

        JSONArray attributionQuestions = new JSONArray();
        attributionQuestions.add("No answer");
        attributionQuestions.add("Google search");
        attributionQuestions.add("Word of mouth");
        attributionQuestions.add("From a friend");
        attributionQuestions.add("Online Advert");
        attributionQuestions.add("Outdoor advert");
        attributionQuestions.add("Social Media");
        attributionQuestions.add("In store");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("font", "Arial");
        jsonObject.put("headerColor", "#bb222f");
        jsonObject.put("backgroundColor", "#ffffff");
        jsonObject.put("mainTextColor", "#4a4a4a");
        jsonObject.put("secondaryTextColor", "#9b9b9b");
        jsonObject.put("mainButtonColor", "#bb222f");
        jsonObject.put("mainTextButtonColor", "#ffffff");
        jsonObject.put("secondaryButtonColor", "#3da5d9");
        jsonObject.put("secondaryTextButtonColor", "#ffffff");
        jsonObject.put("showIcons", false);
        jsonObject.put("showProductListView", false);
        jsonObject.put("enableQueueSelectorPage", false);
        jsonObject.put("showStaffSelector", false);
        jsonObject.put("showStaffDescriptions", false);
        jsonObject.put("showStaffExternalName", false);
        jsonObject.put("showStaffExternalLink", false);
        jsonObject.put("enableNoPreferenceOption", false);
        jsonObject.put("showStaffSelectorInCalendar", false);
        jsonObject.put("showStaffProfileConfirmation", false);
        jsonObject.put("staffPageAfterCalendar", false);
        jsonObject.put("firstNameMandatory", true);
        jsonObject.put("lastNameMandatory", true);
        jsonObject.put("emailMandatory", false);
        jsonObject.put("phoneMandatory", false);
        jsonObject.put("postcodeOption", "DO_NOT_SHOW");
        jsonObject.put("showCustomerTitles", false);
        jsonObject.put("smsOptional", false);
        jsonObject.put("doNotShowProductPageIfOnlyOneProduct", false);
        jsonObject.put("enableCookiePolicy", false);
        jsonObject.put("cookiePolicyLink", "http,//docs.qudini.com/pdf/Qudini-CookiePolicy.pdf");
        jsonObject.put("defaultLatitude", 0);
        jsonObject.put("defaultLongitude", 0);
        jsonObject.put("showAttributionQuestion", false);
        jsonObject.put("logo", "public/images/style-template/default-logos/logo_on_primary.png");
        jsonObject.put("defaultStartDate", epochTime);
        jsonObject.put("minStartDate", epochTime);
        jsonObject.put("attributionQuestions", attributionQuestions);

        return jsonObject;
    }

    //PRIVATE METHODS

    private JSONArray populateVenuesArray(String merchantName, List<String> venuesNames) {

        log.debug("Starting to construct the venues object to associate to the booking widget setup");

        JSONArray bwVenuesInfo = new JSONArray();

        for (String venueName : venuesNames) {

            Integer venueId = parseInt(qudiniAppResponseDataUtils.getVenueIdByName(merchantName, venueName));

            JSONObject bwVenueInfo = new JSONObject();
            bwVenueInfo.put("accessState", "LIVE");
            bwVenueInfo.put("defaultCountryCode", "GB");
            bwVenueInfo.put("storeId", "");
            bwVenueInfo.put("name", venueName);
            bwVenueInfo.put("id", venueId);

            bwVenuesInfo.add(bwVenueInfo);

        }

        log.debug(String.format("Constructed the venues object array, obtained the following object: %n%s", bwVenuesInfo.toString()));

        return bwVenuesInfo;
    }

    private JSONArray populateProductsArray(String merchantName, List<String> productsNames) {

        log.debug("Starting to construct the products object to associate to the booking widget setup");

        JSONArray bwProductsInfo = new JSONArray();

        for (String productName : productsNames) {

            Integer productId = parseInt(qudiniAppResponseDataUtils.getProductIdByProductNameForMerchantId(merchantName, productName));
            Integer productAvgServeTimeMin = parseInt(qudiniAppResponseDataUtils.getProductAverageServeTimeByProductNameForMerchantId(merchantName, productName));

            JSONObject bwProductInfo = new JSONObject();
            bwProductInfo.put("averageServeTimeMinutes", productAvgServeTimeMin);
            bwProductInfo.put("iconId", null);
            bwProductInfo.put("name", productName);
            bwProductInfo.put("id", productId);

            bwProductsInfo.add(bwProductInfo);

        }

        log.debug(String.format("Constructed the venues object array, obtained the following object: %n%s", bwProductsInfo.toString()));

        return bwProductsInfo;

    }

}

