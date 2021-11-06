package com.lb.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String[] keys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();

        String link = "https://hacker-news.firebaseio.com/v0/topstories.json";
        try {
            task.execute(link).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                System.out.println("RESULT " + result);
                keys = result.split(",");

                for (int i = 1; i < 21; i++) {
                    System.out.println("Key number: " + i + " " + keys[i]);
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json = new JSONObject(result);
                String weather = json.getString("weather");
                JSONArray arr = new JSONArray(weather);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject part = arr.getJSONObject(i);
                    Log.i("weather", part.getString("main"));
                    Log.i("description", part.getString("description"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}