package com.example.chaeyk.snsaccounttest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chaeyk on 2016-01-10.
 */
public class HttpClient {

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

    public void report(String url, String id, String token) {
        call(url + "?id=" + id + "&token=" + token);
    }
}
