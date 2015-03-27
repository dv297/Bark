package edu.sc.cse.rdc.bark;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button submitButton;
    Button refreshButton;
    EditText editText;
    ListView itemListView;
    ArrayAdapter<String> adapter;

    ArrayList<String> list;

    JSONParser jsonParser;


    String base_url = "http://grand-thought-88702.appspot.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        submitButton = (Button) findViewById(R.id.submitButton);
        refreshButton = (Button) findViewById(R.id.refreshButton);
        editText = (EditText) findViewById(R.id.editText);
        itemListView = (ListView) findViewById(R.id.listView);
        list = new ArrayList<String>();

        jsonParser = new JSONParser();


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        itemListView.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(editText.getText().toString());
                editText.setText("");
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                refreshItems();
            }
        });

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem(position);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addItem(String s)
    {
        submitData sd = new submitData(s);
        sd.execute();

//        list.add(s);
        adapter.notifyDataSetChanged();
    }

    private void deleteItem(int index)
    {
        list.remove(index);
        adapter.notifyDataSetChanged();
    }

    private void refreshItems()
    {
        loadData dataTask = new loadData();
        dataTask.execute();
    }

    class loadData extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            list.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result)
        {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... arg0)
        {
            List<NameValuePair> nvplist = new ArrayList<NameValuePair>();
//            NameValuePair messagePair = new BasicNameValuePair("message", s);
            JSONArray messageArray = jsonParser.makeHttpRequest(base_url, "GET", nvplist);

            if(messageArray != null)
            {
                for(int i = 0; i < messageArray.length(); i++)
                {
                    try {
                        JSONObject obj = messageArray.getJSONObject(i);
                        list.add(obj.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    class submitData extends AsyncTask<String, String, String> {

        String message;

        public submitData(String _message)
        {
            message = _message;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            list.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result)
        {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... arg0)
        {
            List<NameValuePair> nvplist = new ArrayList<NameValuePair>();
            NameValuePair messagePair = new BasicNameValuePair("message", message);
            nvplist.add(messagePair);
            JSONArray messageArray = jsonParser.makeHttpRequest(base_url + "/postMessage", "GET", nvplist);

            if(messageArray != null)
            {
                for(int i = 0; i < messageArray.length(); i++)
                {
                    try {
                        JSONObject obj = messageArray.getJSONObject(i);
                        list.add(obj.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
