package com.example.chaeyk.snsaccounttest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class MainActivity extends BaseActivity {

    private static final int REQCODE_GOOGLE = 9;

    //////////////////////////////////////////////////////////////
    // For kakao authentication

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //redirectSignupActivity();
            Log.i("TEST", "Kakao session opened: " + Session.getCurrentSession().getAccessToken());
            redirectActivity(KakaoActivity.class);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("TEST", "Kakao session failed");
            if(exception != null) {
                Logger.e(exception);
            }

            tryGoogle();
        }
    }

    private SessionCallback callback;

    /////////////////////////////////////////////////////////////////
    // For Google+ SignIn
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e("TEST", "google connection failed: " + connectionResult.getErrorMessage());
                        setMainLayout();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, GlobalApplication.getGoogleSignInOptions())
                .build();

        tryKakao();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (requestCode == REQCODE_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            processGoogleResult(result);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void tryKakao() {
        Log.i("TEST", "tryKakao");
        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            tryGoogle();
        }
    }

    private void tryGoogle() {
        Log.i("TEST", "tryGoogle()");
        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            processGoogleResult(pendingResult.get());
        } else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async callback.
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    processGoogleResult(result);
                }
            });
        }
    }

    private void processGoogleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GlobalApplication.setGoogleSignInResult(result);
            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();
            Log.i("TEST", "google token: " + idToken);
            redirectActivity(GoogleActivity.class);
        } else {
            Log.e("TEST", "Google SignIn failed: " + result.getStatus());
            setMainLayout();
        }
    }

    private void setMainLayout() {
        setContentView(R.layout.activity_main);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, REQCODE_GOOGLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
}
