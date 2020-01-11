package kr.ac.kumoh.s20161034.mysleep;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

//    public static Context mContext;
//    public static Context getContext() { return this.get; };

    public static final String LOG_TAG = "LOGMainActivity";

    public static final int FRAGMENT_HEART = 0;
    public static final int FRAGMENT_LOCATION = 1;
    public static final int FRAGMENT_ALARM = 2;

    private final int ACTIVITY_LOGIN = 100;
    private final int ACTIVITY_SIGNUP = 101;

    protected SessionManager mSession = null;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //mContext = this;

        mSession = SessionManager.getInstance(this);
        if (mSession.isLogin()) {
            setNavId(mSession.getName());
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setNavId(String id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerView.findViewById(R.id.textNavEmail);
        tv.setText(id);
    }
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        mSession.cancelQueue();
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //----------------------------------------------------------------------------------------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            mViewPager.setCurrentItem(FRAGMENT_HEART);
        } else if (id == R.id.nav_review) {
            mViewPager.setCurrentItem(FRAGMENT_LOCATION);
        } else if (id == R.id.nav_qanda) {
            mViewPager.setCurrentItem(FRAGMENT_ALARM);
        }  else if (id == R.id.nav_signup) {
            if (mSession.isLogin() == false) {
                Intent intent = new Intent(this, SignupActivity.class);
                startActivityForResult(intent, ACTIVITY_SIGNUP);
            }
            else
                Toast.makeText(this, "이미 로그인되어 있습니다.",
                        Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_login) {
            if (mSession.isLogin() == false) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, ACTIVITY_LOGIN);
            }
            else
                Toast.makeText(this, "이미 로그인되어 있습니다.",
                        Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            mSession.Logout();
        } else if (id == R.id.nav_info) {
        } else if (id == R.id.nav_copyright) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch ((requestCode)) {
            case ACTIVITY_LOGIN:
                Log.i(LOG_TAG, "ACTIVITY_LOGIN");

                if (mSession.isLogin()) { // 로그인 성공하고 리턴 경우
                    Log.i(LOG_TAG, "LOGIN");
                    Toast.makeText(this, "로그인되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    TextView tv = (TextView)findViewById(R.id.textNavEmail);
                    tv.setText(mSession.getName());
                }
                else {
                    Log.i(LOG_TAG, "Fail");
                    Toast.makeText(this, "로그인에 실패했습니다.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTIVITY_SIGNUP:
                Log.i(LOG_TAG, "ACTIVITY_SIGNUP");

                if (mSession.isLogin()) { // 회원가입 성공하고 리턴 경우
                    Log.i(LOG_TAG, "SIGNUP");
                    Toast.makeText(this, "회원 가입되었습니다.",
                            Toast.LENGTH_SHORT).show();
                    TextView tv = (TextView)findViewById(R.id.textNavEmail);
                    tv.setText(mSession.getName());
                }
                else {
                    Log.i(LOG_TAG, "Fail");
                    Toast.makeText(this, "회원 가입에 실패했습니다.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("hello");
            return rootView;
        }
    }
    //==============================================================================================
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAGMENT_HEART:
                    return new MyHeartFragment();
                case FRAGMENT_LOCATION:
                    return new MyLocationFragment();
                case FRAGMENT_ALARM:
                    return new MyAlarmFragment();
                default:
                    return new MyFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }
}
