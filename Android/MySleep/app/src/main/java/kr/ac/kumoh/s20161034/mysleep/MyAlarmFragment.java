package kr.ac.kumoh.s20161034.mysleep;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import kr.ac.kumoh.s20161034.mysleep.R;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


public class MyAlarmFragment extends Fragment {

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
    private static final String TAG_PHONENUMBER = "phone_number";
    public static String first_lati ;
    public static String first_longi ;
    public String phone_number;



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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //-----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_notice, container, false);


        context = container.getContext();

        permission = (Button) rootView.findViewById(R.id.permission); //권한확인
        alarm_type1 = (Spinner) rootView.findViewById(R.id.alarm_type1);
        textView = (TextView) rootView.findViewById(R.id.phone_no);


        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 사용자의 OS 버전이 마시멜로우 이상인지 체크한다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                    int permissionResult1 = (checkSelfPermission(getContext(),Manifest.permission.CALL_PHONE));
                    int permissionResult2 = (checkSelfPermission(getContext(),Manifest.permission.SEND_SMS));

                    //전화 권한---------------------------------------------------------------------------
                    if (permissionResult1 == PackageManager.PERMISSION_DENIED) {

                        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("권한이 필요합니다.")
                                    .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속 하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                // CALL_PHONE 권한을 Android OS에 요청한다.
                                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                                            }
                                        }
                                    })
                                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        // 최초로 권한을 요청할 때
                        else {
                            // CALL_PHONE 권한을 Android OS에 요청한다.
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                        }
                    }
                    // CALL_PHONE의 권한이 있을 때
                    else {
                        Toast.makeText(context, "전화권한있음", Toast.LENGTH_LONG).show();
                        Log.i("전화권한있음","111111111111");
                    }

                    //문자권한------------------------------------------------------------------------------
                    if (permissionResult2 == PackageManager.PERMISSION_DENIED) {

                        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("권한이 필요합니다.")
                                    .setMessage("이 기능을 사용하기 위해서는 단말기의 \"문자보내기\" 권한이 필요합니다. 계속 하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                // SEND_SMS 권한을 Android OS에 요청한다.
                                                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1000);
                                            }
                                        }
                                    })
                                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        // 최초로 권한을 요청할 때
                        else {
                            // SEND_SMS 권한을 Android OS에 요청한다.
                            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1000);
                        }
                    }
                    // SEND_SMS의 권한이 있을 때
                    else {
                        Toast.makeText(context, "문자권한있음", Toast.LENGTH_LONG).show();
                        Log.i("SMS 권한 있음","11111111111");
                    }
                }
                // 마시멜로우 미만의 버전일 때
                else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1000);
                }
                Log.i("전화 걸고 나서", "999999999999999999999999");
            }

        });

        Log.i("전화종료후?", "5555555555555555555");
        personList = new ArrayList<HashMap<String, String>>();
        getData2("http://192.168.1.5/PHP_connection3.php"); //수정 필요
        getData("http://192.168.1.5/PHP_connection2.php"); //수정 필요


        getData("http://192.168.1.5/PHP_connection2.php"); //수정 필요
        testStart();

        //스위치=============================
        alarm_type1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position== 0){ //전화
                    alarm = 0;
                    showList();
                    Log.i("전화","00");
                }
                else if(position==1){ //문자
                    alarm= 1;
                    showList();
                    Log.i("문자","1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * 권한 요청에 대한 응답을 이곳에서 가져온다.
     *
     * @param requestCode 요청코드
     * @param permissions 사용자가 요청한 권한들
     * @param grantResults 권한에 대한 응답들(인덱스별로 매칭)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1000)
        {

            // 요청한 권한을 사용자가 "허용" 했다면...
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("권한 허용후 ", "6666666666666666666");
                // Add Check Permission
                if (checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    Log.i("권한 허용후 ", "77777777777777777");
                }
            } else {
                Toast.makeText(context, "권한요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }

            Log.i("권한 허용후 ", "8888888888888888888");
        }
    }


    //-----------------------------------------------------------------------------------------------
    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                int beat = c.getInt(TAG_BEAT);
                String lati = c.getString(TAG_LATI);
                String longi = c.getString(TAG_LONGI);


                Log.d(lati, "위도");
                Log.d(longi, "경도");
                Log.d(first_lati, "<<위도11>>");
                Log.d(first_longi, "<<경도11>>");

                if(beat<=90 && !lati.equals(first_lati) && !longi.equals(first_lati)){
                    // 메뉴 버튼 강제 실행하여 보이기
                    Log.i("들어왔왔왔ㄷ듣듣", "들 어 왔 당");
                    if(alarm == 0){
                        Log.i("전화보낸당","전화보냄");

                        String tel = "tel:" + phone_number;
                        Log.d(tel,"전화보냄");
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                        startActivity(intent);
                        Log.d("옹오옹ㅇ","옹옹ㅇ옹");
                    }
                    else if (alarm == 1){
                        Log.i("문자보낸당","문자보냄");

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phone_number, null, "환자님의 움직임이 감지되었습니다", null, null);
                    }
                    break;
                }else{
                    Log.i("안들어왔지롱~~", "안들어왔다구~~");
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
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
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
    protected void showList2() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);

                first_lati = c.getString(TAG_LATI);
                first_longi = c.getString(TAG_LONGI);
                phone_number = c.getString(TAG_PHONENUMBER);
                textView.setText(phone_number);

                Log.d(phone_number, "연락할 번호");
                Log.d(first_lati, "위도11");
                Log.d(first_longi, "경도11");



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //-----------------------------------------------------------------------------------------------
    public void getData2(String url) {
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
                showList2();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}