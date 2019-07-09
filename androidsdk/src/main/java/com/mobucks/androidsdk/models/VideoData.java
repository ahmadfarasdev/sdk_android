package com.mobucks.androidsdk.models;
import com.mobucks.androidsdk.tools.vastparser.models.VideoTrackEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoData {
    private String videoUrl;
    private String clickUrl;
    private long currentPosition;
    private long duration;
    private boolean fullscreen;
    private Map<VideoTrackEvent,List<String>> eventsMap =new HashMap<>();
    private Map<Integer,List<String>> timeBasedEventMap =new HashMap<>();
    private List<String> videoClickTrackers = new ArrayList<>();

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public Map<VideoTrackEvent, List<String>> getEventsMap() {
        return eventsMap;
    }

    public void setEventsMap(Map<VideoTrackEvent, List<String>> eventsMap) {
        this.eventsMap = eventsMap;
    }

    public Map<Integer,List<String>> getTimeBasedEventMap() {
        return timeBasedEventMap;
    }

    public void setTimeBasedEventMap( Map<Integer,List<String>> timeBasedEventMap) {
        this.timeBasedEventMap = timeBasedEventMap;
    }

    public List<String> getVideoClickTrackers() {
        return videoClickTrackers;
    }

    public void setVideoClickTrackers(List<String> videoClickTrackers) {
        this.videoClickTrackers = videoClickTrackers;
    }
}
