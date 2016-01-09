package com.example.chaeyk.snsaccounttest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by chaeyk on 2016-01-07.
 */
public class GlobalApplication extends Application {

    private static class KakaoSDKAdapter extends KakaoAdapter {
        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Activity getTopActivity() {
                    return GlobalApplication.getCurrentActivity();
                }

                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }

    private static volatile GlobalApplication instance;
    private static volatile Activity currentActivity;

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    public static GlobalApplication getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("this application does not inherit com.example.chaeyk.googleplaytest.GlobalApplication");

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }
}