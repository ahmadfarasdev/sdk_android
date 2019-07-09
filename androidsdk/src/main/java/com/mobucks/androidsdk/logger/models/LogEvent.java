package com.mobucks.androidsdk.logger.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class LogEvent {
    private String id;
    private String message;
    private String stackTrace;
    private LogSeverity level;
    private long timestamp;


    public LogEvent(String message, LogSeverity level) {
        id= UUID.randomUUID().toString();
        timestamp=System.currentTimeMillis();  //TODO Get timestamp from a ntp server
        this.message = message;
        this.level = level;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogSeverity getLevel() {
        return level;
    }

    public void setLevel(LogSeverity level) {
        this.level = level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("type","sdk-android");
        jsonObject.put("message",message);
        jsonObject.put("stackTrace",stackTrace);
        jsonObject.put("otmLevel",level.ordinal());
        jsonObject.put("otmCode","400");

        return  jsonObject;

    }

}
