package com.nerdapplabs.forumapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nerdapplabs.forumapp.MSOAuth2;
import com.nerdapplabs.forumapp.R;
import com.nerdapplabs.forumapp.oauth.constant.OAuthConstant;
import com.nerdapplabs.forumapp.oauth.constant.ReadForumProperties;
import com.nerdapplabs.forumapp.utility.LocaleHelper;
import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         *  This method call is to change
         *  the language of the application.
         */
        try {
            Properties properties = ReadForumProperties.getPropertiesValues();
            String savedLocale = Preferences.getString(OAuthConstant.APP_LOCALE, Locale.getDefault().getLanguage());
            if (!savedLocale.equals(properties.getProperty("LOCALE"))) {
                Log.e(TAG, "Locale changed in properties file:" + properties.getProperty("LOCALE"));
                changeAppLanguage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create Navigation drawer and inflate layout
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        // TODO: handle navigation

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        MSOAuth2.getInstance().setConnectivityListener(this);

        // Get logged in UserName
        String userName = Preferences.getString(OAuthConstant.USERNAME, null);
        if (null != userName) {
            updateNavigationHeaderView(userName);
        }
    }

    /**
     * Method to update Navigation Drawer header values for logged in user.
     * Display User name
     * @param userName String  userName
     */
    private void updateNavigationHeaderView(String userName) {
        View headerView = navigationView.getHeaderView(0);
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.drawer_username);
        drawerUsername.setText(userName);
    }

    /**
     * Method for open login options page
     *
     * @param v view type
     */
    public void onDrawerHeaderClick(View v) {

        // Get logged in UserName
        String userName = Preferences.getString(OAuthConstant.USERNAME, null);
        Intent intent;
        if (null != userName) {
            intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), LoginActionsActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        NetworkConnectivity.showNetworkConnectMessage(MainActivity.this, isConnected);
    }

    /**
     * Method to change the application language
     */
    private void changeAppLanguage() {
        try {
            Properties properties = ReadForumProperties.getPropertiesValues();
            Log.d(TAG, "languageCode " + properties.getProperty("LOCALE"));
            LocaleHelper.setLocale(this, properties.getProperty("LOCALE"));
            // Set 'locale' settings in application preferences
            Preferences.putString(OAuthConstant.APP_LOCALE, properties.getProperty("LOCALE"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}