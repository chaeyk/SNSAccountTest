package com.example.chaeyk.snsaccounttest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class GoogleActivity extends BaseActivity {

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e("TEST", "google connection failed: " + connectionResult.getErrorMessage());
                        redirectMainActivity();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, GlobalApplication.getGoogleSignInOptions())
                .build();

        setContentView(R.layout.google_layout);

        TextView tvID = (TextView) findViewById(R.id.tvID);
        tvID.setText(GlobalApplication.getGoogleSignInResult().getSignInAccount().getId());

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        GlobalApplication.setGoogleSignInResult(null);
                        redirectMainActivity();
                    }
                });
            }
        });

    }
}
