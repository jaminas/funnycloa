package com.example.testdeeplink;

import android.util.Log;

public class P6
{
    public static void parseDeeplinkSting(String s)
    {
        String[] stringParts = s.split("://");
        s  = stringParts[stringParts.length - 1];
        stringParts = s.split("\\?");
        s  = stringParts[stringParts.length - 1];

        String[] pairs = s.split("&");

        for (String pair: pairs)
        {
            String[] kv = pair.split("=");
            if (kv.length == 2)
            {
                if ("dl".equals(kv[0])) {
                    MainActivity.dl = kv[1];
                } else if ("sub_id_1".equals(kv[0])) {
                    MainActivity.sub_id_1 = kv[1];
                } else if ("sub_id_2".equals(kv[0])) {
                    MainActivity.sub_id_2 = kv[1];
                } else if ("sub_id_3".equals(kv[0])) {
                    MainActivity.sub_id_3 = kv[1];
                } else if ("sub_id_4".equals(kv[0])) {
                    MainActivity.sub_id_4 = kv[1];
                } else if ("sub_id_5".equals(kv[0])) {
                    MainActivity.sub_id_5 = kv[1];
                }
            }
        }
    }
}
