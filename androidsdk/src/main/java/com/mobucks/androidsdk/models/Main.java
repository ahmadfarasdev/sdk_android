package com.mobucks.androidsdk.models;

public class Main {
    private String IMGTEXT;

    private String IMGURL;

    private String TITLE;

    private String IMGLINK;

    private Info INFO;

    public String getIMGTEXT() {
        return IMGTEXT;
    }

    public void setIMGTEXT(String IMGTEXT) {
        this.IMGTEXT = IMGTEXT;
    }

    public String getIMGURL() {
        return IMGURL;
    }

    public void setIMGURL(String IMGURL) {
        this.IMGURL = IMGURL;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getIMGLINK() {
        return IMGLINK;
    }

    public void setIMGLINK(String IMGLINK) {
        this.IMGLINK = IMGLINK;
    }

    public Info getINFO() {
        return INFO;
    }

    public void setINFO(Info INFO) {
        this.INFO = INFO;
    }

    @Override
    public String toString() {
        return "ClassPojo [IMGTEXT = " + IMGTEXT + ", IMGURL = " + IMGURL + ", TITLE = " + TITLE + ", IMGLINK = " + IMGLINK + "]";
    }
}