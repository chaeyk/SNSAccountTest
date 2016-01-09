package com.example.chaeyk.snsaccounttest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

public class FacebookActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_layout);

        TextView tvID = (TextView) findViewById(R.id.tvID);
        tvID.setText(GlobalApplication.getFacebookAccessToken().getUserId());

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                GlobalApplication.setFacebookAccessToken(null);
                redirectMainActivity();
            }
        });
    }
}
