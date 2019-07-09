package com.mobucks.androidsdk.tools.vastparser.models;

public class Linear {
    private String skipoffset;

    private String duration;

    private MediaFiles mediaFiles;

    private TrackingEvents trackingEvents;

    private VideoClicks videoClicks;

    public String getSkipoffset() {
        return skipoffset;
    }

    public void setSkipoffset(String skipoffset) {
        this.skipoffset = skipoffset;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public MediaFiles getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(MediaFiles mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public void setTrackingEvents(TrackingEvents trackingEvents) {
        this.trackingEvents = trackingEvents;
    }

    public VideoClicks getVideoClicks() {
        return videoClicks;
    }

    public void setVideoClicks(VideoClicks videoClicks) {
        this.videoClicks = videoClicks;
    }
}
