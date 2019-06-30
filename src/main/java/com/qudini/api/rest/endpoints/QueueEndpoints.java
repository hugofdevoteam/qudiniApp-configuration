package com.qudini.api.rest.endpoints;

public final class QueueEndpoints {

    private QueueEndpoints() {
    }

    public static final String ADD_VENUE_QUEUE = "/venue-add-queue-submit";

    public static final String LIST_QUEUES_ADMIN_DATA = "/api/venue/%s/queuesAdminData";

    public static final String LIST_QUEUE_DETAILS = "/api/queue/%s";

    public static final String CHANGE_QUEUE_DETAILS = "/api/queue/details";

    public static final String QUEUES_INFO_FOR_EACH_VENUE = "/api/v3/venues/%s/queues";

}
