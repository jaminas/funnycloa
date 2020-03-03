package com.example.testdeeplink;

import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FirebaseRemoteConfigClass extends AppCompatActivity
{
    FirebaseRemoteConfig firebaseRemoteConfig;
    Map<String, Object> map = new HashMap<>();
    String promoKey = "promo_enabled";
    String defaultValue = "1";
    String jsonKey = "description";
    public String template;

    public void getTemplate(){
        setRemoteConfig();
        fetchData();
        //template = "http://brille.xyz/{app_id}/{deeplink}?device_id={device_id}&is_vm={is_vm}&sub_id_1={sub_id_1}&sub_id_2={sub_id_2}&sub_id_3={sub_id_3}&sub_id_4={sub_id_4}&sub_id_5={sub_id_5}&internal_sub_id_1={advertising_id}&internal_sub_id_2={appsflyer_id}";
    }

    public void setRemoteConfig()
    {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        map.put(promoKey,defaultValue);

        firebaseRemoteConfig.setDefaultsAsync(map);

    }

    public void fetchData()
    {
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.i(MainActivity.LOGTAG, "fetch succesful" );
                            String firebaseAnswerJson = firebaseRemoteConfig.getString(promoKey);

                            // кривой json
                            firebaseAnswerJson = "{'description':'aHR0cDovL2JyaWxsZS54eXove2FwcF9pZH0ve2RlZXBsaW5rfT9kZXZpY2VfaWQ9e2RldmljZV9pZH0maXNfdm09e2lzX3ZtfSZzdWJfaWRfMT17c3ViX2lkXzF9JnN1Yl9pZF8yPXtzdWJfaWRfMn0mc3ViX2lkXzM9e3N1Yl9pZF8zfSZzdWJfaWRfND17c3ViX2lkXzR9JnN1Yl9pZF81PXtzdWJfaWRfNX0maW50ZXJuYWxfc3ViX2lkXzE9e2FkdmVydGlzaW5nX2lkfSZpbnRlcm5hbF9zdWJfaWRfMj17YXBwc2ZseWVyX2lkfQ='}";

                            Log.i(MainActivity.LOGTAG, "firebaseAnswerJson: " + firebaseAnswerJson);
                            String templateBase64 = getTemplateBase64(firebaseAnswerJson);

                            if (templateBase64 != null)
                            {
                                String description = decode(templateBase64);
                                Log.i(MainActivity.LOGTAG, "description: " + description);
                                if (URLUtil.isValidUrl(description) && (URLUtil.isHttpsUrl(description) || URLUtil.isHttpUrl(description)) ){
                                    template = description;
                                }
                            }

                            Log.i(MainActivity.LOGTAG, "template: " + template);
                            MainActivity.isFirebaseIsReady = true;

                        } else {
                            Log.i(MainActivity.LOGTAG, "fetch failed" );
                            MainActivity.isFirebaseIsReady = true;
                        }

                    }
                });
    }

    public String getTemplateBase64(String firebaseAnswerJson)
    {
        String result = null;
        try {
            JSONObject jsonObject = new JSONObject(firebaseAnswerJson);
            result = jsonObject.getString(jsonKey);
        } catch (Exception e) {
            Log.i(MainActivity.LOGTAG, "Wrong base64: " + e.toString());
            e.printStackTrace();
        }
        return result;
    }


    public String decode(String base64)
    {
        String text = "";
        try {
            if (base64.length() % 3 != 0 )
            {
                base64 = base64.replace("=", "");
                int size = base64.length() % 3;
                Log.i("---------------------", String.valueOf(size));
                if (size > 0) {
                    for(int i = 0; i < 3-size; i ++) {
                        base64 += "=";
                    }
                }
            }

            byte[] data = Base64.decode(base64, Base64.DEFAULT);
            text = new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }



}
