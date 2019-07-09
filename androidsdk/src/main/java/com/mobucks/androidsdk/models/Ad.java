package com.mobucks.androidsdk.models;

import com.mobucks.androidsdk.Globals;
import com.mobucks.androidsdk.tools.Tools;
import com.mobucks.androidsdk.tools.vastparser.models.Vast;
import com.mobucks.androidsdk.tools.vastparser.models.VideoTrackEvent;
import com.mobucks.androidsdk.tools.vastparser.models.VideoClicks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ad {
    private String title;
    private String imageText;
    private String imageUrl;
    private String imageLink;
    private String vast;
    private String bannerHtml;
    private long timeCreated;
    private boolean clicked =false;
    private VideoData videoData;
    private String tagId;

    public  Ad() {
        timeCreated = System.currentTimeMillis();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageText() {
        return imageText;
    }

    public void setImageText(String imageText) {
        this.imageText = imageText;
    }

    public String getImageUrl() {
        return  imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getVast() {
        return vast;
    }

    public void setVast(String vast) {
        this.vast = vast;
    }

    public String getBannerHtml() {
        return bannerHtml;
    }

    public void setBannerHtml(String bannerHtml) {
        this.bannerHtml = bannerHtml;
    }

    public boolean isVideoAd(){
        return vast!=null && !vast.isEmpty();
    }
    public boolean hasNativeContent(){
        return bannerHtml == null;
    }

    public boolean isValid(){
        long timeSinceInit = System.currentTimeMillis() - timeCreated;
        return  timeSinceInit < Globals.AD_TTL;
    }

    public boolean isValidVideo(){
        return  isVideoAd() && videoData!=null && videoData.getClickUrl() !=null && videoData.getVideoUrl() !=null;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void loadVideoData(Vast vast){
        videoData = new VideoData();
        videoData.setVideoUrl(vast.getBestMediaFile().getValue());
        Map<VideoTrackEvent,List<String>> eventsListHashMap =  vast.getTrackingEvents();
        VideoClicks videoClicks = vast.getVideoClicks();
        long duration = Tools.vastDurationToMillis(vast.getDuration());
        Map<Integer,List<String>> eventTimeTrackers =  Tools.getTimeEventsTrackers(eventsListHashMap,duration);
        videoData.setEventsMap(eventsListHashMap);
        videoData.setTimeBasedEventMap(eventTimeTrackers);
        videoData.setDuration(duration);
        videoData.setClickUrl(videoClicks.getClickThrough());
        videoData.setVideoClickTrackers(videoClicks.getClickTracking());

    }

    public VideoData getVideoData() {
        return videoData;
    }

    public void setVideoData(VideoData videoData) {
        this.videoData = videoData;
    }

    public boolean hasValidMediaFile() {
        return videoData!=null &&  videoData.getVideoUrl()!=null;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
