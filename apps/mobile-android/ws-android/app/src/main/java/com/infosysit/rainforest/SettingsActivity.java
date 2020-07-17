package com.infosysit.rainforest;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static com.infosysit.rainforest.ColorUtil.changeWindowColor;
import static com.infosysit.sdk.Constants.connectivityMessage;
import static com.infosysit.sdk.UtilityJava.isOnline;

/**
 * Created by akansha.goyal on 5/8/2018.
 */

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ImageView lexLogo = (ImageView) findViewById(R.id.yourlogo);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        changeWindowColor(this);
//        lexLogo.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(SettingsActivity.this,connectivityMessage,Toast.LENGTH_LONG).show();
////                if(isOnline(SettingsActivity.this)){
////                    Util.navigationWebView(SettingsActivity.this,"/home");
////                }
////                else {
////                    Toast.makeText(SettingsActivity.this,connectivityMessage,Toast.LENGTH_LONG).show();
////                }
//
//            }
//        });
    }

    public void goCatalog(View view) {
        if(isOnline(SettingsActivity.this)){
            Util.navigateToPage(SettingsActivity.this,"/catalog");
        }

        else {
            Toast.makeText(SettingsActivity.this,connectivityMessage,Toast.LENGTH_LONG).show();

        }
    }

    public void goHome(View view) {
        if(isOnline(SettingsActivity.this)){
            Util.navigateToPage(SettingsActivity.this,"/home");
        }
        else {
            Toast.makeText(SettingsActivity.this,connectivityMessage,Toast.LENGTH_LONG).show();
        }
    }

    public void goBrand(View view) {
        if(isOnline(SettingsActivity.this)){
            Util.navigateToPage(SettingsActivity.this,"/catalog/marketing/Brand Assets");
        }

        else {
            Toast.makeText(SettingsActivity.this,connectivityMessage,Toast.LENGTH_LONG).show();

        }
    }

    public void goInfyTv(View view) {
        if(isOnline(SettingsActivity.this)){
            Util.navigateToPage(SettingsActivity.this,"/infytv");
        }

        else {
            Toast.makeText(SettingsActivity.this,connectivityMessage,Toast.LENGTH_LONG).show();

        }
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_download_over)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.action_logout_key)));



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {


        Log.d("PreferenceManagers",preference.getKey());
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        preference.setOnPreferenceClickListener(sBindPreferenceSummaryToClickListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(),false));

    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d("PreferenceManagers",String.valueOf(newValue));
            String stringValue = newValue.toString();


            return true;
        }
    };


    private static Preference.OnPreferenceClickListener sBindPreferenceSummaryToClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.getKey().equalsIgnoreCase("Logout")){
//                if(isOnline(SettingsActivity.this)){
//                    Util.navigationWebView(mContext,"/feedback/application");
//                }
//                else {
//                    Toast.makeText(mContext,connectivityMessage,Toast.LENGTH_LONG).show();
//                }
                if(HomeActivity.loginPage != null){
                    Log.d("PreferenceManagers","Previous "+HomeActivity.loginPage.getUrl());
                }
            }
            return false;
        }
    };


}