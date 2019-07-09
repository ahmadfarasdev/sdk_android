package com.mobucks.androidsdk.models;


public class Content {
    private Main main;
    private AdError error;
    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public AdError getError() {
        return error;
    }

    public void setError(AdError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ClassPojo [main = " + main + "]";
    }
}