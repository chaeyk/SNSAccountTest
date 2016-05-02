package com.example.chaeyk.snsaccounttest;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by chaeyk on 2016-01-11.
 */
public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("registration");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i("TEST", "token: " + token);
            new HttpClient().push(token);
        } catch (IOException e) {
            Log.e("TEST", "registration failed", e);
        }
    }
}
