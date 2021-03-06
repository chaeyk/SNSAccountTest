package com.example.chaeyk.snsaccounttest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.facebook.AccessToken;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by chaeyk on 2016-01-07.
 */
@ReportsCrashes(
        formUri = "http://crash.idolchamp.com:5984/acra-testapp/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="idolreporter",
        formUriBasicAuthPassword="reporter12#",
        // Your usual ACRA configuration
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
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
            throw new IllegalStateException("this application does not inherit com.example.chaeyk.snsaccounttest.GlobalApplication");

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);

        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
    }

    private static GoogleSignInOptions googleSignInOptions;

    public static GoogleSignInOptions getGoogleSignInOptions() {
        return googleSignInOptions;
    }

    public static void setGoogleSignInOptions(GoogleSignInOptions options) {
        googleSignInOptions = options;
    }

    private static GoogleSignInResult googleSignInResult;

    public static GoogleSignInResult getGoogleSignInResult() {
        return googleSignInResult;
    }

    public static void setGoogleSignInResult(GoogleSignInResult result) {
        googleSignInResult = result;
    }
}
