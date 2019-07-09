package com.mobucks.androidsdk.models;

import android.text.TextUtils;

import com.mobucks.androidsdk.tools.Tools;

public class AdResponse {
    private Content content;
    private String vast;
    public static final String NO_DATA = "NoDATA";

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Ad getAd() {
        Ad ad = new Ad();
        Main main = content.getMain();
        ad.setImageLink(Tools.transformFailUrl(main.getIMGLINK()));
        ad.setImageText(main.getIMGTEXT());
        ad.setImageUrl(Tools.transformFailUrl(main.getIMGURL()));
        ad.setTitle(main.getTITLE());
        if (main.getINFO() != null) {
            ad.setTagId(main.getINFO().getTagId());
        }
        return ad;
    }

    public boolean isNotFilled() {
        // return (getAdError()!=null && getAdError().getCode().equals("633")) || content.getMain().getIMGURL().equals("NoDATA") ;
        // return content.getMain() == null || TextUtils.isEmpty(content.getMain().getIMGURL());
        Ad ad = getAd();
        if (ad.isVideoAd() && ad.getVideoData() == null) {
            return true;
        } else if (TextUtils.isEmpty(ad.getImageUrl()) || NO_DATA.equals(ad.getImageUrl())  || NO_DATA.equals(ad.getImageLink())) {
            return true;
        }
        return this.getAdError() != null && "633".equals (this.getAdError().getCode()) && NO_DATA.equals(this.getAdError().getDescription());
    }

    public boolean hasError() {
        return getAdError() != null && !(getAdError().getCode().equals("0"));
    }

    public AdError getAdError() {
        return content.getError();
    }

    @Override
    public String toString() {
        return "ClassPojo [content = " + content + "]";
    }
}
