package com.qudini.api.rest.json.paths;

public final class QueuePaths {

    private QueuePaths() {
    }

    public static final String QUEUE_DETAILS_SNS_TOPIC_VALUE = "$.queueData.queue.snsTopicArn";

    //Works also for retrieving data from the response of getQueueInfoPerVenueId
    public static final String RETRIEVE_QUEUE_DETAILS_QUEUE_ID_FOR_QUEUE_WITH_NAME = "$.[?(@.name=='%s')].id";

    //Works also for retrieving data from the response of getQueueInfoPerVenueId
    public static final String RETRIEVE_QUEUE_DETAILS_QUEUE_IDENTIFIER_FOR_QUEUE_WITH_NAME = "$.[?(@.name=='%s')].identifier";

    public static final String RETRIEVE_QUEUES_NAMES_FOR_VENUE_QUEUES = "$.[*].name";

    // THIS COME FROM THE RESPONSE OF getQueueInfoPerVenueId USED ON QUEUE ARRAY OBJ TO LINK PRODUCT TO QUEUES

    public static final String RETRIEVE_VENUE_NAME_FROM_QUEUES_IN_VENUE_RESPONSE = "$.[?(@.name=='%s')].venueName";
}
