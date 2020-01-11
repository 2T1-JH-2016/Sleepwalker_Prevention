package kr.ac.kumoh.s20161034.mysleep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MyLocationFragment extends Fragment implements OnMapReadyCallback {

    View rootView;
    MapView mapView;


    String myJSON;
    private Context context;

    //----------------------------------------------------------------------------------------------
    private Button permission;
    Spinner alarm_type1;

    public int alarm = -1; //알람타입
    TextView textView;
    //----------------------------------------------------------------------------------------------

    private static final String TAG_RESULTS = "result";
    private static final String TAG_BEAT = "beat";
    private static final String TAG_LATI = "lati";
    private static final String TAG_LONGI = "longi";

    public static String lati ;
    public static String longi ;
    public static double d_lati;
    public static double d_longi;
    GoogleMap mMap;

    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    private TimerTask second;
    private final Handler handler = new Handler();
    //-----------------------------------------------------------------------------------------------
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
        timer.schedule(second, 0, 5000);

    }
    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {
                getData("http://192.168.1.5/PHP_connection2.php");

            }
        };
        handler.post(updater);
    }

    //-----------------------------------------------------------------------------------------------

    public MyLocationFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_maps, container, false);
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        personList = new ArrayList<HashMap<String, String>>();
        getData("http://192.168.1.5/PHP_connection2.php"); //수정 필요


        getData("http://192.168.1.5/PHP_connection2.php"); //수정 필요
        testStart();
        return rootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(this.getActivity());

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(d_lati, d_longi), 14);

        mMap.animateCamera(cameraUpdate);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(d_lati, d_longi)));

    }


    //-----------------------------------------------------------------------------------------------
    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                int beat = c.getInt(TAG_BEAT);
                lati = c.getString(TAG_LATI);
                longi = c.getString(TAG_LONGI);

                d_lati = Double.parseDouble(lati);
                d_longi = Double.parseDouble(longi);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(d_lati, d_longi), 14);
                mMap.animateCamera(cameraUpdate);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(d_lati, d_longi)));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //-----------------------------------------------------------------------------------------------
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
                    return "";
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