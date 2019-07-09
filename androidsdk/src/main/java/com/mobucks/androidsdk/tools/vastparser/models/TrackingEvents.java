package com.mobucks.androidsdk.tools.vastparser.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingEvents {
    Map<VideoTrackEvent,List<String>> events =new HashMap<>();

    public Map<VideoTrackEvent, List<String>> getEvents() {
        return events;
    }

    public void setEvents(Map<VideoTrackEvent, List<String>> events) {
        this.events = events;
    }
}
