package com.example.chaeyk.snsaccounttest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class MainActivity extends BaseActivity {

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //redirectSignupActivity();
            Log.i("TEST", "session opened: " + Session.getCurrentSession().getAccessToken());
            redirectActivity(KakaoActivity.class);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_main);
        }
    }

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
}
