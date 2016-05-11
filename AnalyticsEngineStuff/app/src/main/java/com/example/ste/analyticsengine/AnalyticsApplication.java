package com.example.ste.analyticsengine;

import android.app.Activity;
import android.app.Application;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ska23 on 10/05/16.
 */
public class AnalyticsApplication extends Application {

    private ArrayList<AnalyticsButtonInfo> buttonsPressed = new ArrayList<>();

    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacks() {

                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                    }

                    @Override
                    public void onActivityStarted(Activity activity) {

                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        if (activity instanceof BSkyBBaseActivity && ((BSkyBBaseActivity) activity).getPageName() != null) {
                            BSkyBBaseActivity skyActivity = (BSkyBBaseActivity) activity;
                            AnalyticsHelper helper = skyActivity.getAnalyticsHelper();

                            configureAnalyticsTouchEvent(skyActivity, helper);

                            if (buttonsPressed.size() >= 0) {
                                AnalyticsButtonInfo info = buttonsPressed.get(buttonsPressed.size());

                                helper.trackLinkClick("Action Name", info.getContainingActivityName(), info.getButtonText(), info.getLinkArea(), null);
                                helper.trackState(skyActivity.getPageName(), skyActivity.getPageName(),  null);

                                buttonsPressed.clear();
                            }
                        }
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {

                    }
                });
    }

    private void configureAnalyticsTouchEvent(BSkyBBaseActivity activity, final AnalyticsHelper helper) {

        ViewGroup views =  (ViewGroup) ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);

        final BSkyBBaseActivity baseActivity = activity;

        findAnalyticsViews(views, baseActivity);


    }

    private ArrayList<AnalyticsButtonInfo> getButtonsPressed() {
        return buttonsPressed;
    }


    private void findAnalyticsViews(ViewGroup views, final Activity baseActivity) {
        if (views != null) {
            for (int i = 0; i < views.getChildCount(); i++) {
                View v = views.getChildAt(i);

                if (v instanceof ViewGroup) {
                    findAnalyticsViews((ViewGroup) v, baseActivity);
                }

                if (v instanceof Button) {

                    v.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            //Add this to an array of buttons pressed in Service Application
                            // When a new Screen is launched check the name of the last button pressed!
                            //On a new activity created,

                            AnalyticsButtonInfo analyticsButtonInfo = new AnalyticsButtonInfo(baseActivity, buttonsPressed.get(buttonsPressed.size()).toString(), baseActivity.getLinkArea());

                            getButtonsPressed().add(analyticsButtonInfo);

                            return false;
                        }
                    });
                } else if (v instanceof ListView) {

                    v.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            final ListView list = (ListView) v;

                            Rect rect = new Rect();
                            int childCount = list.getChildCount();
                            int[] listViewCoords = new int[2];
                            list.getLocationOnScreen(listViewCoords);
                            int x = (int) event.getRawX() - listViewCoords[0];
                            int y = (int) event.getRawY() - listViewCoords[1];
                            View child;


                            for (int i = 0; i < childCount; i++) {
                                child = list.getChildAt(i);
                                child.getHitRect(rect);
                                if (rect.contains(x, y)) {

                                    View listItem = list.getChildAt(i);


                                    if (listItem instanceof ViewGroup) {
                                        ViewGroup listItemGroup = (ViewGroup) listItem;
                                        iterateOverListItem(listItemGroup, baseActivity);
                                    }
                                }
                            }
                            return false;
                        }
                    });
                }

            }
        }
    }

    private void iterateOverListItem(ViewGroup viewGroup, BSkyBBaseActivity baseActivity ) {
        int listItemChildCount = viewGroup.getChildCount();
        int i;
        for (i = 0; i < listItemChildCount; i++) {

            View listItemChild = viewGroup.getChildAt(i);
            if (listItemChild instanceof ViewGroup) {
                iterateOverListItem((ViewGroup) listItemChild, baseActivity);
            }
            if (listItemChild instanceof TextView) {
                AnalyticsButtonInfo analyticsButtonInfo = new AnalyticsButtonInfo(baseActivity, ((TextView) listItemChild).getText().toString(), baseActivity.getLinkArea());
                getButtonsPressed().add(analyticsButtonInfo);
                return;
            }

        }
    }
}
