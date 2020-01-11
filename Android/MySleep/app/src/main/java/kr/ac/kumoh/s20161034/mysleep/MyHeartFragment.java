package kr.ac.kumoh.s20161034.mysleep;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.kumoh.s20161034.mysleep.R;

public class MyHeartFragment extends Fragment{


    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";


    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    TextView tv;
    private TimerTask second;
    private final Handler handler = new Handler();
    public void testStart() {

        final int[] timer_sec = {0};
        int count = 0;

        second = new TimerTask() {

            @Override
            public void run() {
                Log.i("Test", "Timer start");
                Update();

                timer_sec[0]++;
            }
        };
        Timer timer = new Timer();
        timer.schedule(second, 0, 1000);
    }
    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {
                getData("http://192.168.1.5/PHP_connection.php");
            }
        };
        handler.post(updater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beat, container, false);

        tv = (TextView)rootView.findViewById(R.id.beat);
        personList = new ArrayList<HashMap<String, String>>();
        getData("http://192.168.1.5/PHP_connection.php"); //수정 필요
        testStart();
        return rootView;
    }



    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String id = c.getString(TAG_ID);
                Log.d(c.getString(TAG_ID), "하하하하");
                tv.setText(id);

                Log.d(id, "호홓호호호");
                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_ID, id);


                personList.add(persons);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }


            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;

                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


}
