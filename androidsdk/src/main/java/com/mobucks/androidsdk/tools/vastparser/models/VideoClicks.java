package com.mobucks.androidsdk.tools.vastparser.models;

import java.util.ArrayList;
import java.util.List;

public class VideoClicks {

    private String clickThrough;
    private List<String> clickTracking = new ArrayList<>();
    private List<String> customClick = new ArrayList<>();

    public String getClickThrough() {
        return clickThrough;
    }

    public void setClickThrough(String clickThrough) {
        this.clickThrough = clickThrough;
    }

    public List<String> getClickTracking() {
        return clickTracking;
    }

    public void setClickTracking(List<String> clickTracking) {
        this.clickTracking = clickTracking;
    }

    public List<String> getCustomClick() {
        return customClick;
    }

    public void setCustomClick(List<String> customClick) {
        this.customClick = customClick;
    }
}
