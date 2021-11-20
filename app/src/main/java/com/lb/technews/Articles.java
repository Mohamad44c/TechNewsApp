package com.lb.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Articles extends AppCompatActivity {

    ArrayList<String> keyList = new ArrayList<>();
    ArrayList<String> article_titles = new ArrayList<>();
    ArrayList<String> article_urls = new ArrayList<>();
    SQLiteDatabase articlesDB2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        Bundle bundle = getIntent().getExtras();
        keyList = (ArrayList<String>) bundle.getStringArrayList("keysActual");


        try {
            for (int i = 1; i < keyList.size(); i++) {
                String articleURL = "https://hacker-news.firebaseio.com/v0/item/" + keyList.get(i) + ".json?print=pretty";
                DownloadTask task = new DownloadTask();
                task.execute(articleURL);
            }

            articlesDB2 = this.openOrCreateDatabase("TechNewsDB2", MODE_PRIVATE, null);
            articlesDB2.execSQL("CREATE TABLE IF NOT EXISTS news2 (article_id INTEGER, article_title VARCHAR, article_url VARCHAR)");

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
//                System.out.println(result);
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

                article_titles.add(a_title);

                try {

//                    String sql = "INSERT INTO news2 (article_id, article_title, article_url) VALUES (?, ?, ?)";
//                    SQLiteStatement statement = articlesDB2.compileStatement(sql);
//                    statement.bindString(1, String.valueOf(a_id));
//                    statement.bindString(2, a_title);
//                    statement.bindString(3, a_url);
//
//                    statement.execute();

                    Cursor c = articlesDB2.rawQuery("SELECT * FROM news2", null);

                    int article_id_index = c.getColumnIndex("article_id");
                    int article_title_index = c.getColumnIndex("article_title");
                    int article_url_index = c.getColumnIndex("article_url");
                    c.moveToFirst();

                    while (c != null) {
//                        Log.i("Article ID", Integer.toString(c.getInt(article_id_index)));
//                        Log.i("Article title", c.getString(article_title_index));
//                        Log.i("Article URL", c.getString(article_url_index));
                        article_urls.add(c.getString(article_url_index));
                        c.moveToNext();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, article_titles);
                myView.setAdapter(adapter);

                myView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent webviewIntent = new Intent(Articles.this, Webview.class);

                        String link = article_urls.get(i);
                        webviewIntent.putExtra("link", link);
                        System.out.println("URL sent to webview " + link);
                        startActivity(webviewIntent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}