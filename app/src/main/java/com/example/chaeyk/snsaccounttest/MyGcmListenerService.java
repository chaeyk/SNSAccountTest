package com.example.chaeyk.snsaccounttest;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.UnsupportedEncodingException;

/**
 * Created by chaeyk on 2016-01-11.
 */
public class MyGcmListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("TEST", "From: " + from);
        Log.d("TEST", "Message: " + message);

        try {
            new HttpClient().noti(from, message);
        } catch (UnsupportedEncodingException e) {
            Log.e("TEST", "httpclient.noti() failed", e);
        }
    }
}
