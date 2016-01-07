package com.example.chaeyk.googleplaytest;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.AgeAuthResponse;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

/**
 * Created by chaeyk on 2016-01-07.
 */
public class KakaoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSuccess(UserProfile result) {
                Logger.i("success");
                setContentView(R.layout.kakao_layout);

                TextView tvID = (TextView) findViewById(R.id.tvID);
                tvID.setText(Long.toString(result.getId()));

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
                Logger.e(errorResult.getErrorMessage());
                redirectMainActivity();
            }

            @Override
            public void onNotSignedUp() {
                Logger.e("not signed up");
                redirectMainActivity();
            }
        });
    }
}
