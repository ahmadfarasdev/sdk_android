package com.mobucks.androidsdk.tools.vastparser.models;

import java.util.ArrayList;
import java.util.List;

public class InLine {
    private String adSystem;

    private List<String> impressions = new ArrayList<>();

    private String adTitle;

    private Creatives creatives;

    public String getAdSystem() {
        return adSystem;
    }

    public void setAdSystem(String adSystem) {
        this.adSystem = adSystem;
    }


    public List<String> getImpressions() {
        return impressions;
    }

    public void setImpressions(List<String> impressions) {
        this.impressions = impressions;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public Creatives getCreatives() {
        return creatives;
    }

    public void setCreatives(Creatives creatives) {
        this.creatives = creatives;
    }
}
