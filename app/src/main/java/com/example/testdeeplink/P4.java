package com.example.testdeeplink;

import android.net.Uri;
import android.util.Log;
import java.util.regex.PatternSyntaxException;

public class P4 {
    static String regex1 = "\\{";
    static String regex2 = "\\}";
    static String app_id_key = regex1+ "app_id" + regex2;
    static String deeplink_key = regex1+ "deeplink" + regex2;
    static String device_id_key = regex1+ "device_id" + regex2;
    static String is_vm_key = regex1+ "is_vm" + regex2;
    static String sub_id_1_key = regex1+ "sub_id_1" + regex2;
    static String sub_id_2_key = regex1+ "sub_id_2" + regex2;
    static String sub_id_3_key = regex1+ "sub_id_3" + regex2;
    static String sub_id_4_key = regex1+ "sub_id_4" + regex2;
    static String sub_id_5_key = regex1+ "sub_id_5" + regex2;

    static String internal_sub_id_1_key = regex1+ "advertising_id" + regex2;
    static String internal_sub_id_2_key = regex1+ "appsflyer_id" + regex2;

    static String sub1_map_key = "sub_id_1";
    static String sub2_map_key = "sub_id_2";
    static String sub3_map_key = "sub_id_3";
    static String sub4_map_key = "sub_id_4";
    static String sub5_map_key = "sub_id_5";


    public static String fillTemplate(String template)
    {
        String outputLink = template;
        try
        {
            outputLink = outputLink.replaceAll(app_id_key, MainActivity.appId);
            outputLink = outputLink.replaceAll(deeplink_key, MainActivity.dl);
            outputLink = outputLink.replaceAll(device_id_key, MainActivity.deviceId);
            outputLink = outputLink.replaceAll(is_vm_key, "1");

            outputLink = outputLink.replaceAll(sub_id_1_key, MainActivity.sub_id_1);
            outputLink = outputLink.replaceAll(sub_id_2_key, MainActivity.sub_id_2);
            outputLink = outputLink.replaceAll(sub_id_3_key, MainActivity.sub_id_3);
            outputLink = outputLink.replaceAll(sub_id_4_key, MainActivity.sub_id_4);
            outputLink = outputLink.replaceAll(sub_id_5_key, MainActivity.sub_id_5);

            outputLink = outputLink.replaceAll(internal_sub_id_1_key, MainActivity.advertId);

            outputLink = outputLink.replaceAll(internal_sub_id_2_key, ""); // MainActivity.appsflyerId
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return outputLink;
    }

}
