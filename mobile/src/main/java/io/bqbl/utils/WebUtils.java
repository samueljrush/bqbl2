package io.bqbl.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import io.bqbl.MyApplication;
import io.bqbl.data.CacheManager;

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
      Log.e(MyApplication.logTag(WebUtils.class.getSimpleName()), "Volley error encountered", error);
    }
  };

  public static void setImageRemoteUri(final ImageView imageView, final String url) {
    Bitmap cachedInstance = CacheManager.getInstance().getBitmapFromDiskCache(url);
    if (cachedInstance != null) {
      imageView.setImageBitmap(cachedInstance);
    }
    ImageRequest request = new ImageRequest(url,
        new Response.Listener<Bitmap>() {
          @Override
          public void onResponse(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            CacheManager.getInstance().addBitmapToDiskCache(url, bitmap);
          }
        }, 0, 0, imageView.getScaleType(), null,
        new Response.ErrorListener() {
          public void onErrorResponse(VolleyError error) {
            //imageView.setImageResource(R.drawable.image_load_error);
          }
        });
    MyApplication.getInstance().addToRequestQueue(request);
  }
}
