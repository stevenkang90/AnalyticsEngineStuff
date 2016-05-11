package com.example.ste.analyticsengine;

import com.bskyb.app.activity.BSkyBBaseActivity;

/**
 * Created by ska23 on 06/03/16.
 */
public class AnalyticsButtonInfo {

    private String linkArea;
    private String buttonText;
    private BSkyBBaseActivity baseActivity;

    public AnalyticsButtonInfo(BSkyBBaseActivity baseActivity, String buttonText, String linkArea) {
        this.linkArea = linkArea;
        this.buttonText = buttonText;
        this.baseActivity = baseActivity;

    }

    public String getContainingActivityName() {
        return baseActivity.getPageName();
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getLinkArea() {
        return linkArea;
    }
}
