package com.lb.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

//      here I get the arraylist of API Keys
        Bundle bundle = getIntent().getExtras();
        keyList = (ArrayList<String>) bundle.getStringArrayList("keysActual");

//        retrieved parsed keys from main and printing 20
//        for (int i = 0; i < keyList.size(); i++) {
//            System.out.println("FROM MAIN: KEY # " + i + " " + keyList.get(i));
//        }


        for (int i = 0; i < keyList.size(); i++) {
            String articleURL = "https://hacker-news.firebaseio.com/v0/item/" + keyList.get(i) + ".json?print=pretty";
            DownloadTask task = new DownloadTask();
            task.execute(articleURL);
        }

//////////////////////////////////////////////////////////////////////////////////////////////
//        creating and filling database
        try {
//            SQLiteDatabase db = this.openOrCreateDatabase("technewsdb", MODE_PRIVATE, null);
//            db.execSQL("CREATE TABLE IF NOT EXISTS news (article_id INT(8), article_title VARCHAR, article_url VARCHAR)");
//            db.execSQL("INSERT INTO links(article_id, article_title, article_url) VALUES('a_id', 'a_title', 'a_url')");
//            Cursor c = db.rawQuery("SELECT * FROM news", null);
//
//            int article_id_index = c.getColumnIndex("article_id");
//            int article_title_index = c.getColumnIndex("article_title");
//            int article_url_index = c.getColumnIndex("article_url");
//            c.moveToFirst();
//
//            while (c != null) {
//                Log.i("Article ID", Integer.toString(c.getInt(article_id_index)));
//                Log.i("Article title", c.getString(article_title_index));
//                Log.i("Article URL", c.getString(article_url_index));
//                c.moveToNext();
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
//////////////////////////////////////////////////////////////////////////////////////////////
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

                int a_id = json.getInt("id");
                String a_title = json.getString("title");
                String a_url = json.getString("url");


//                Log.i("ID ", "" + a_id);
//                Log.i("Title ", a_title);
//                Log.i("URL ", a_url);

                article_titles.add(a_title);
//                System.out.println("Article Size: " + article_titles.size());
//                System.out.println("Article URL: " + a_url);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, article_titles);
                myView.setAdapter(adapter);


//                onclick will open the website corresponding to the title click
                myView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent webviewIntent = new Intent(Articles.this, Webview.class);
                        webviewIntent.putExtra("a_url", a_url);
                        startActivity(webviewIntent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}