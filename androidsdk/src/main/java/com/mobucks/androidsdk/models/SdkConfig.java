package com.mobucks.androidsdk.models;

public class SdkConfig {
    private String uid;
    private String adServerApi;
    private String memberId;
    private String tagId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAdServerApi() {
        return adServerApi;
    }

    public void setAdServerApi(String adServerApi) {
        this.adServerApi = adServerApi;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
