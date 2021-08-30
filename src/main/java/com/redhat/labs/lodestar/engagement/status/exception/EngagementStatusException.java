package com.redhat.labs.lodestar.engagement.status.exception;

public class EngagementStatusException extends  RuntimeException{

    public EngagementStatusException(String message) {
        super(message);
    }
    public EngagementStatusException(String message, Exception ex) {
        super(message, ex);
    }
}
