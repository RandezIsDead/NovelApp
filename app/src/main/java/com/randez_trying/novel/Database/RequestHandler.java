package com.randez_trying.novel.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

public class RequestHandler {

    @SuppressLint("StaticFieldLeak")
    private static RequestHandler instance;
    private RequestQueue requestQueue;
    private final Context context;

    public RequestHandler(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestHandler getInstance(Context context) {
        if (instance == null) instance = new RequestHandler(context);
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        RetryPolicy policy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 120,
                10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        getRequestQueue().add(request);
    }
}