package io.bqbl.utils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import io.bqbl.MyApplication;

public final class WebUtils {

  private WebUtils() {
  }

  public static final String TAG = WebUtils.class.getSimpleName();

  public static Request<JSONObject> postRequest(String url, JSONObject post, Response.Listener<JSONObject> listener) {
    return new JsonObjectRequest(Request.Method.POST, url, post, listener, DEFAULT_ERROR_LISTENER);
  }

  public static Request<JSONObject> getRequest(String url, Response.Listener<JSONObject> listener) {
    return new JsonObjectRequest(Request.Method.GET, url, null, listener, DEFAULT_ERROR_LISTENER);
  }

  private static final Response.ErrorListener DEFAULT_ERROR_LISTENER = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e(MyApplication.getTag(WebUtils.class.getSimpleName()), "Volley error encountered", error);
    }
  };
}
