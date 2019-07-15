package com.qudini.api.requests.forms;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public final class QueueDetails {

    public List<NameValuePair> defaultPropertiesForQueueDetailsWithBookingEnabled(
            String queueId,  //Supposedly it is the same as id
            String identifier,
            String queueName,
            String averageServeTime, //Supposedly it is the same as averageServeTimeMinutes
            String snsTopicArn
    ) {

        List<NameValuePair> paramsAsNameValuePairList = new ArrayList<>();

        paramsAsNameValuePairList.add(new BasicNameValuePair("id", queueId));
        paramsAsNameValuePairList.add(new BasicNameValuePair("queueId", queueId));
        paramsAsNameValuePairList.add(new BasicNameValuePair("identifier", identifier));
        paramsAsNameValuePairList.add(new BasicNameValuePair("name", queueName));
        paramsAsNameValuePairList.add(new BasicNameValuePair("averageServeTime", averageServeTime));
        paramsAsNameValuePairList.add(new BasicNameValuePair("averageServeTimeMinutes", averageServeTime));
        paramsAsNameValuePairList.add(new BasicNameValuePair("snsTopicArn", snsTopicArn));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isBookingEnabled", "true"));

        //May be useful to know for future changes
        paramsAsNameValuePairList.add(new BasicNameValuePair("advanceBookingsMinutes", "0"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("advanceBookingsWeeks", "4"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("bookingStart", "10"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("bookingTimings", "TIME"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerEmailRequired", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerEmailVisible", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerQuestionEnabled", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerNameRequired", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerNameVisible", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerPostcodeRequired", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerPostcodeVisible", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerSurnameRequired", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerSurnameVisible", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("mobileVisible", "true"));

        //It may never be changed for initial config purpose
        paramsAsNameValuePairList.add(new BasicNameValuePair("isBookingAllowed", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isWalkinEnabled", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isWalkinAllowed", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isRestaurant", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("queuePlanClassName", "service.plan.BookedQueuePlan"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("staffBookingAvailability", "0"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("smsNotifier", "Nexmo"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("hasSensitiveData", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("hoursAfterDeletion", "24"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("sensitiveDataSetAtMerchantLevel", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("queueType", "2"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("bufferSpace", "-1"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("hideCollectionView", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isWebPushEnabledForCustomerAdded", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isWebPushEnabledForCustomerJoining", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("advisorCannotChooseCustomer", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("allowedToToggleCustomerDescriptionOption", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("applyTicketNumber", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("captureOrderNumber", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("conciergeAcceptReject", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerDescriptionEnabled", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerNameRequiredKiosk", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("customerTimings", "BOTH"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("finishReminder", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("forceAdvisorToReportOutcome", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isRetainTicketNumber", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isTabletCollectionEnabled", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("isWalkoutDuringCustomerAddEnabled", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("numberWithDuplicatedNameKiosk", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("outcomeReportingBooking", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("outcomesEnabled", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("pagerRequired", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("pagerVisible", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("queueMessagesThresholdMinutes", "0"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("queueMessagesThresholdPosition", "0"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("removeCustomerEnabled", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("requestOutcomeAdvisors", "true"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("requestOutcomeConcierge", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("requiredMpn", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("secondsAcceptReject", "60"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("serverAcceptReject", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("serverAllowedBreak", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("serverCannotViewProducts", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("showAllUncollected", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("skipJoinMessage", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("smsRestricted", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("staffTimings", "BOTH"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("ticketNumberTag", "A"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("unreadMessagesForQueue", "0"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("nameOption[value]", "2"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("nameOption[text]", "Visible and mandatory"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("surnameOption[value]", "1"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("surnameOption[text]", "Visible and non-mandatory"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("emailOption[value]", "1"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("emailOption[text]", "Visible and non-mandatory"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("postcodeOption[value]", "0"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("postcodeOption[text]", "Not visible"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("pagerOption[value]", " 0"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("pagerOption[text]", "Not visible"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("mobileOption[value]", "1"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("mobileOption[text]", "Visible and non-mandatory"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("outcomeReportingWalkin", "false"));
        paramsAsNameValuePairList.add(new BasicNameValuePair("tagFormListJson", "[]"));

        return paramsAsNameValuePairList;

    }

}
