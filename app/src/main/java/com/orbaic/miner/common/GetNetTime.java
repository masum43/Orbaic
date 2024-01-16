package com.orbaic.miner.common;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
public class GetNetTime {

    private long timestamp = 0;
    String responseBody = "";

    public long getNetTime(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            StringRequest request = new StringRequest(Request.Method.GET, "https://timeapi.io/api/Time/current/coordinate?latitude=23.777176&longitude=90.399452",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            responseBody = s;
                            System.out.println(responseBody);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println(volleyError.getCause());
                }
            });
            Volley.newRequestQueue(context).add(request);

            JSONObject object = new JSONObject(responseBody);

            String t = object.getString("dateTime").toString();
            System.out.println(t);
            String truncatedString = t.substring(0, 26);


            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                LocalDateTime dateTime = LocalDateTime.parse(truncatedString, formatter);
                long longValue = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                System.out.println("Long value: " + longValue);
                timestamp = longValue;
            }



        } catch (Exception e) {
            System.err.println("Error getting current time: " + e);
        }

        return timestamp;
    }
    public boolean isError(){
        if(timestamp < 0){
            return false;
        }else{
            return true;
        }
    }
}
