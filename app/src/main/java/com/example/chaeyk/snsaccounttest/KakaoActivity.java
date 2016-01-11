package com.example.chaeyk.snsaccounttest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

public class KakaoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSuccess(UserProfile result) {
                setContentView(R.layout.kakao_layout);

                Log.i("TEST", "Kakao id: " + Long.toString(result.getId()));
                Log.i("TEST", "Kakao token: " + Session.getCurrentSession().getAccessToken());

                TextView tvID = (TextView) findViewById(R.id.tvID);
                tvID.setText(Long.toString(result.getId()));

                new HttpClient().report("http://dev1.idolchamp.com:3009/kakao",
                        Long.toString(result.getId()), Session.getCurrentSession().getAccessToken());

                findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                redirectMainActivity();
                            }
                        });
                    }
                });
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("TEST", errorResult.getErrorMessage());
                redirectMainActivity();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("TEST", "not signed up");
                redirectMainActivity();
            }
        });
    }
}
