package com.example.chaeyk.snsaccounttest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by chaeyk on 2016-01-10.
 */
public class HttpClient {

    public static final String URL = "http://dev1.idolchamp.com:3009/";

    AsyncTask<String, Void, String> task;

    public void call(String url) {
        task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(false);
                    conn.setDoInput(true);

                    String body = "";

                    BufferedReader br = new BufferedReader(new InputStreamReader( conn.getInputStream(), "utf8" ));
                    String line;
                    while( (line = br.readLine()) != null ) {
                        body += line;
                    }

                    conn.disconnect();
                    Log.i("TEST", body);
                    return body;
                } catch (Exception e) {
                    Log.e("TEST", "http client throwed exception", e);
                    return null;
                }
            }
        };
        task.execute(url);
    }

    public void report(String service, String id, String token) {
        call(URL + service + "?id=" + id + "&token=" + token);
    }

    public void push(String token) {
        call(URL + "push?token=" + token);
    }

    public void noti(String from, String message) throws UnsupportedEncodingException {
        call(URL + "noti?from=" + from + "&message=" + URLEncoder.encode(message, "utf8"));
    }
}
