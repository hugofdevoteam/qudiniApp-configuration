package com.qudini.api.rest.json.paths;

public final class QueuePaths {

    private QueuePaths(){}

    public static final String QUEUE_DETAILS_SNS_TOPIC_VALUE = "$.queueData.queue.snsTopicArn";

    public static final String QUEUE_DETAILS_QUEUE_ID = "$.[?(@.name=='%s')].id";

    public static final String QUEUE_DETAILS_QUEUE_IDENTIFIER = "$.[?(@.name=='%s')].identifier";
}
