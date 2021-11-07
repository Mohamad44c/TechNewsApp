package com.lb.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Articles extends AppCompatActivity {
    ArrayList<String> keyList;
    ArrayList<String> article_titles = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);


        Bundle bundle = getIntent().getExtras();
        keyList = (ArrayList<String>) bundle.getStringArrayList("keysActual");

//        retrieved parsed keys from main and printing 20
//        for (int i = 0; i < keyList.size(); i++) {
//            System.out.println("FROM MAIN: KEY # " + i + " " + keyList.get(i));
//        }


        String articleURL = "https://hacker-news.firebaseio.com/v0/item/" + keyList.get(1) + ".json?print=pretty";
        String articleURL2 = "https://hacker-news.firebaseio.com/v0/item/" + keyList.get(2) + ".json?print=pretty";
        DownloadTask task = new DownloadTask();
        task.execute(articleURL);
        DownloadTask task2 = new DownloadTask();
        task2.execute(articleURL2);


//        creating and filling database
//        SQLiteDatabase db = this.openOrCreateDatabase("technewsdb", MODE_PRIVATE, null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS links (article_id INT(8), article_title VARCHAR, article_url VARCHAR)");
//        db.execSQL("INSERT INTO links(article_id, article_title, article_url) VALUES()");
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
                ListView myView = (ListView) findViewById(R.id.article_List);


                JSONObject json = new JSONObject(result);

                int article_id = json.getInt("id");
                String article_title = json.getString("title");
                String article_url = json.getString("url");


                Log.i("ID ", "" + article_id);
                Log.i("Title ", article_title);
                Log.i("URL ", article_url);

                article_titles.add(article_title);
                System.out.println("Article Size: " + article_titles.size());
                System.out.println("Article URL: " + article_url);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, article_titles);
                myView.setAdapter(adapter);

                myView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        WebView webView = (WebView) findViewById(R.id.webView);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new WebViewClient());
                        webView.loadUrl("https://en.wikipedia.org/wiki/Wikipedia:Wikipedia_Signpost/2021-10-31/Opinion");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}