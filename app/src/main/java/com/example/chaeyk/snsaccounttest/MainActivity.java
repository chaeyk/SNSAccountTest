package com.example.chaeyk.snsaccounttest;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

    private class KakaoSessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            // id는 requestMe() 해야 나온다.
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

    private KakaoSessionCallback kakaoSessionCallback;

    /////////////////////////////////////////////////////////////////
    // For Google+ SignIn
    GoogleApiClient googleApiClient;

    /////////////////////////////////////////////////////////////////
    // For Facebook
    CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kakaoSessionCallback = new KakaoSessionCallback();
        Session.getCurrentSession().addCallback(kakaoSessionCallback);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e("TEST", "google connection failed: " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, GlobalApplication.getGoogleSignInOptions())
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                redirectActivity(FacebookActivity.class);
            }

            @Override
            public void onCancel() {
                Log.w("TEST", "Facebook canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("TEST", "Facebook failed: " + error.toString());
            }
        });

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        tryKakao();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(kakaoSessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // kakao
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // google
        if (requestCode == REQCODE_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            processGoogleResult(result);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

        // facebook
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            processGoogleResult(pendingResult.get());
        } else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async kakaoSessionCallback.
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
            Log.i("TEST", "Google id: " + acct.getId());
            Log.i("TEST", "Google token: " + acct.getIdToken());
            new HttpClient().report("google", acct.getId(), acct.getIdToken());
            redirectActivity(GoogleActivity.class);
        } else {
            Log.e("TEST", "Google SignIn failed: " + result.getStatus());
            tryFacebook();
        }
    }

    private void tryFacebook() {
        Log.i("TEST", "tryFacebook()");
        if (AccessToken.getCurrentAccessToken() != null) {
            redirectActivity(FacebookActivity.class);
        } else {
            setMainLayout();
        }
    }

    private void setMainLayout() {
        setContentView(R.layout.activity_main);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, REQCODE_GOOGLE);
            }
        });
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, -1)
                        .show();
            } else {
                Log.i("TEST", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
