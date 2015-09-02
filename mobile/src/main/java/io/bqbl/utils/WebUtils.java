package io.bqbl.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
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

  public static Request<JSONObject> postJsonRequest(String url, JSONObject post, Response.Listener<JSONObject> listener) {
    return new JsonObjectRequest(Request.Method.POST, url, post, listener, defaultErrorListener(url));
  }

  public static Request<JSONObject> getJsonRequest(String url, Response.Listener<JSONObject> listener) {
    return new JsonObjectRequest(Request.Method.GET, url, null, listener, defaultErrorListener(url));
  }

  private static Response.ErrorListener defaultErrorListener(final String url) {
    return new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(MyApplication.logTag(WebUtils.class.getSimpleName()), "Volley error encountered for url " + url, error);
      }
    };
  }

  public static void setImageRemoteUri(final ImageView imageView, final String url) {
    getBitmapRemoteUri(imageView, url, new Listener<Bitmap>() {
      @Override
      public void onResult(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
      }
    });
  }

  public static void setBackgroundRemoteUri(final View view, final String url) {
    getBitmapRemoteUri(view, url, new Listener<Bitmap>() {
      @Override
      public void onResult(Bitmap bitmap) {
        view.setBackground(new BitmapDrawable(view.getResources(), bitmap));
      }
    });
  }

  private static void getBitmapRemoteUri(final View view, final String url, final Listener<Bitmap> bitmapListener) {
    Bitmap cachedInstance = CacheManager.getInstance().getBitmapFromDiskCache(url);
    if (cachedInstance != null) {
      bitmapListener.onResult(cachedInstance);
    }

    ImageView.ScaleType scaleType = (view instanceof ImageView)
        ? ((ImageView) view).getScaleType()
        : ImageView.ScaleType.CENTER;

    ImageRequest request = new ImageRequest(url,
        new Response.Listener<Bitmap>() {
          @Override
          public void onResponse(Bitmap bitmap) {
            bitmapListener.onResult(bitmap);
            CacheManager.getInstance().addBitmapToDiskCache(url, bitmap);
          }
        }, 0, 0, scaleType, null,
        new Response.ErrorListener() {
          public void onErrorResponse(VolleyError error) {
            //imageView.setImageResource(R.drawable.image_load_error);
          }
        });
    MyApplication.getInstance().addToRequestQueue(request);
  }
}
