package com.orbaic.miner;


import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PushNotificationExtra {

    private Context context;

    public PushNotificationExtra(Context context) {
        this.context = context;
    }

    public void sendNotification(String fcmToken, String title, String body) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            String url = "https://fcm.googleapis.com/fcm/send";
            RequestQueue queue = Volley.newRequestQueue(context);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("to", fcmToken);

            JSONObject notificationData = new JSONObject();
            notificationData.put("title", title);
            notificationData.put("body", body);

            jsonBody.put("notification", notificationData);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Notification sent successfully
                            System.out.println(response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                            error.printStackTrace();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=AAAAu6y0yqU:APA91bHG5FZjTMesMN0GVFFTNPTk94T_px80E8M7wE7hZZ9f8h9NRCI2uE0Ci_xHmNAQr77-b7foa-ijoZ2-kMKw8EirlcBASVjjjGxHO0DP0ph82eVV9M0qL2unpoAXwo1piZXs_zO9");
                    return headers;
                }
            };

            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
