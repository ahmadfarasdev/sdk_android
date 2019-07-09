package com.mobucks.androidsdk.tools.vastparser.models;

import java.util.List;
import java.util.Map;

public class Vast {
    private VastAd ad;
    private String version;

    public VastAd getAd() {
        return ad;
    }

    public void setAd(VastAd ad) {
        this.ad = ad;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getDuration(){
        return ad.getInLine().getCreatives().getCreativeList().get(0).getLinear().getDuration();
    }

    public MediaFile getBestMediaFile() {
        MediaFiles mediaFiles = ad.getInLine().getCreatives().getCreativeList().get(0).getLinear().getMediaFiles();
        return mediaFiles.getMediaFileList().get(0);
    }

    public Map<VideoTrackEvent,List<String>> getTrackingEvents() {
        TrackingEvents trackingEvents = ad.getInLine().getCreatives().getCreativeList().get(0).getLinear().getTrackingEvents();
        return  trackingEvents.getEvents();
    }

    public VideoClicks getVideoClicks(){
        VideoClicks videoClicks = ad.getInLine().getCreatives().getCreativeList().get(0).getLinear().getVideoClicks();
        return  videoClicks;
    }

    public boolean isValidAd(){
        InLine inLine = ad.getInLine();
        if(inLine==null) {
            return  false;
        }
        Creatives creatives = inLine.getCreatives();
        if(creatives==null) {
            return  false;
        }


        if(creatives.getCreativeList()==null || creatives.getCreativeList().isEmpty()) {
            return  false;
        }
        Creative creative =creatives.getCreativeList().get(0);

        if(creative==null){
            return  false;
        }

        Linear linear = creative.getLinear();
        if(linear==null){
            return false;
        }

        MediaFiles mediaFiles = linear.getMediaFiles();
        if(mediaFiles == null || mediaFiles.getMediaFileList() == null || mediaFiles.getMediaFileList().isEmpty()){
            return false;
        }

        return  true;
    }
}
