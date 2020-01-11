package kr.ac.kumoh.s20161034.mysleep;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SessionManager {
    private static SessionManager sInstance;

    public static final String SERVER_ADDR = "http://192.168.123.102/honeysleep/";


    public static final String PREF_NAME = "SessionManagerPref";
    public static final String QUEUE_TAG = "VolleyRequest";
    public static final String LOG_TAG = "LOGSessionManager";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    static Context mContext = null;

    protected RequestQueue mQueue = null;
    protected boolean mIsLogin = false;

    protected String mID = null;
    protected String mName = null;
    protected String mIdToLogin = null;

    protected Activity mCurrentActivity = null;

    //--------------------------------------------------------------
    public static SessionManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SessionManager(context);
        }
        return sInstance;
    }
    //--------------------------------------------------------------
    public SessionManager(Context context){
        SessionManager.mContext = context;
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        mID = pref.getString(KEY_ID, "");
        mName = pref.getString(KEY_NAME, "");
        if (mID.length() > 0 && mName.length() > 0) {
            mIsLogin = true;
        }
        CookieHandler.setDefault(new CookieManager());
        mQueue = Volley.newRequestQueue(context);
    }

    public boolean isLogin() {
        return mIsLogin;
    }

    public String getName() {
        return mName;
    }

    protected void sessionLogin() {
        // mID는 parseNetworkResponse()에서 세팅
        mID = mIdToLogin;
        Log.i(LOG_TAG, "mID = mIdToLogin;");
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_NAME, mIdToLogin);
        editor.commit();
        mIdToLogin = "";
        mIsLogin = true;
        Log.i(LOG_TAG, "mIsLogin = true;");

        ((MainActivity) mContext).setNavId(mID);
    }

    protected void sessionLogout() {
        mID = "";
        mName = "";
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        mIsLogin = false;
        Log.i(LOG_TAG, "mIsLogin = false;");
        ((MainActivity) mContext).setNavId(mID);

    }

    //---------------------------------------------------------------------------
    public void Login(String id, String pass, Activity activity)  {
        String url = SERVER_ADDR + "login.php";

        Map<String, String> params = new HashMap<String, String>();

        params.put("id", id);
        params.put("pw", pass);

        JSONObject jsonObj = new JSONObject(params);
        mIdToLogin = id;
        mCurrentActivity = activity;
        Log.i(LOG_TAG,"jsonObj : "+jsonObj);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG,"Response: " + response.toString());
                        try {
                            if (response.has("status")) {
                                if (response.getString("status").equals("Success")) {
                                    sessionLogin();
                                    mCurrentActivity.setResult(RESULT_OK);
                                    mCurrentActivity.finish();
                                    mCurrentActivity = null;
                                }
                            }
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG,"Error: " + error.getMessage());
                        mCurrentActivity.setResult(RESULT_OK);
                        mCurrentActivity.finish();
                        mCurrentActivity = null;
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG,"getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();

                if (mID.length() > 0 && mName.length() > 0) {
                    String cookie = String.format("id=" + mID + ";name=" + mName);
                    headers.put("Cookie", cookie);
                }
                return headers;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Log.i(LOG_TAG,"parseNetworkResponse()");

                Log.i("response",response.headers.toString());
                Map<String, String> responseHeaders = response.headers;
                String cookie = responseHeaders.get("Set-Cookie");
                if (cookie != null) {
                    Log.i("Set-Cookie", cookie);
                    int p = cookie.indexOf("id=");
                    Log.i("index=", "" + p);
                    if (p >= 0) {
                        mID = cookie.substring(cookie.indexOf("id=") + 3, cookie.length() - 1);
                        int end = mID.indexOf(";");
                        if (end > 0)
                            mID = mID.substring(0, end);
                        Log.i("mID", mID);
                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(KEY_ID, mID);
                        editor.commit();
                    }
                }
                return super.parseNetworkResponse(response);
            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }
//---------------------------------------------------------------------------------------------------------------------------------
    public void Signup(String id, String name, String pass, String sex, String age, String phone, String type, String appkey)  {
        String url = SERVER_ADDR + "signup.php";
//        Log.i(LOG_TAG,"appkey : " + appkey);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("name", name);
        params.put("pw", pass);
        params.put("sex", sex);
        params.put("age", age);
        params.put("phone",phone );
        params.put("type", type);
        params.put("appkey",appkey);

        JSONObject jsonObj = new JSONObject(params);

        mIdToLogin = id;
        Log.i(LOG_TAG,"jsonObj : "+jsonObj);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG,"Response: " + response.toString());
                        try {
                            if (response.has("status")) {
                                if (response.getString("status").equals("Success")) {
                                    sessionLogin();
                                }
                            }
                            else {
                                Log.i(LOG_TAG,"Error: " +
                                        response.getString("error"));
                                if (response.getString("error").
                                        equals("Registered Id")) {
                                    Toast.makeText(mContext, "이미 등록된 아이디 입니다.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG,"Error: " + error.getMessage());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG,"getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();

                if (mID.length() > 0 && mName.length() > 0) {
                    String cookie = String.format("id=" + mID + ";name=" + mName);
                    headers.put("Cookie", cookie);
                }
                return headers;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Log.i(LOG_TAG,"parseNetworkResponse()");

                Log.i("response",response.headers.toString());
                Map<String, String> responseHeaders = response.headers;
                String cookie = responseHeaders.get("Set-Cookie");
                if (cookie != null) {
                    Log.i("Set-Cookie", cookie);
                    int p = cookie.indexOf("id=");
                    Log.i("index=", "" + p);
                    if (p >= 0) {
                        mID = cookie.substring(cookie.indexOf("id=") + 3, cookie.length() - 1);
                        int end = mID.indexOf(";");
                        if (end > 0)
                            mID = mID.substring(0, end);
                        Log.i("mID", mID);
                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(KEY_ID, mID);
                        editor.commit();
                    }
                }
                return super.parseNetworkResponse(response);
            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    //---------------------------------------------------------------------------
    public void Logout() {
        String url = SERVER_ADDR + "logout.php";

        Map<String, String> params = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, jsonObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG,"Response: " + response.toString());
                        sessionLogout();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG,"Error: " + error.getMessage());

                        mID = "";
                        mName = "";
                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        mIsLogin = false;
                        Log.i(LOG_TAG, "mIsLogin = false;");
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG,"getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();

                if (mID.length() > 0 && mName.length() > 0) {
                    String cookie = String.format("id=" + mID + ";email=" + mName);
                    headers.put("Cookie", cookie);
                }
                return headers;
            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    //---------------------------------------------------------------------------
    public void cancelQueue() {
        if (mQueue != null) {
            mQueue.cancelAll(QUEUE_TAG);
        }
    }
}

