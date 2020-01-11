package kr.ac.kumoh.s20161034.mysleep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class SignupActivity extends AppCompatActivity {
    // UI references.
    private EditText mIdView;
    private EditText mNameView;
    private EditText mPasswordView;
    private EditText mSexView;
    private EditText mAgeView;
    private EditText mPhoneView;
    private EditText mTypeView;
    private EditText mAppkeyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mIdView     = (EditText) findViewById(R.id.email);
        mNameView  = (EditText) findViewById(R.id.name);
        mPasswordView  = (EditText) findViewById(R.id.password);
        mSexView  = (EditText) findViewById(R.id.sex);
        mAgeView  = (EditText) findViewById(R.id.age);
        mPhoneView  = (EditText) findViewById(R.id.phone);
        mTypeView  = (EditText) findViewById(R.id.type);
        mAppkeyView = (EditText)findViewById(R.id.appkey);
        mAppkeyView.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                int kkey;
                String key = "";
                for(int i=0;i<32;i++) {
                    kkey = (int) (Math.random() * 16);
                    key+=(Integer.toHexString(kkey));
                    if (i % 2 != 0) {
                        key+=(" ");
                    }
                }
                Toast.makeText(getApplicationContext(),"당신의 사용자 키는"+key+"입니당", Toast.LENGTH_LONG).show();
                mAppkeyView.setText(key);
            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    //-----------------------------------------------------------------------------
    private void attemptLogin() {
        // Reset errors.
        mIdView.setError(null);
        mNameView.setError(null);
        mPasswordView.setError(null);
        mSexView.setError(null);
        mAgeView.setError(null);
        mPhoneView.setError(null);
        mTypeView.setError(null);
        mAppkeyView.setError(null);

        // Store values at the time of the login attempt.
        String id = mIdView.getText().toString();
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String sex = mSexView.getText().toString();
        String age = mAgeView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String type = mTypeView.getText().toString();
        String appkey = mAppkeyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(id)) {
            mIdView.setError(getString(R.string.error_field_required));
            focusView = mIdView;
            cancel = true;
        } else if (!isIdValid(id)) {
            mIdView.setError(getString(R.string.error_invalid_id));
            focusView = mIdView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            SessionManager.getInstance(getApplicationContext()).
                    Signup(id, name, password, sex, age, phone, type, appkey);
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean isIdValid(String id) {
        //TODO: Replace this with your own logic
        return id.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

}
