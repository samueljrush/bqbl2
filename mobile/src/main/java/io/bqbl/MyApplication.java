package io.bqbl;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {

  public static final String TAG = MyApplication.class
      .getSimpleName();
  private static final String TAG_PREFIX = "BQBLio | ";

  private RequestQueue mRequestQueue;
  private ImageLoader mImageLoader;

  private static MyApplication mInstance;

  private static Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

  @Override
  public void onCreate() {
    super.onCreate();
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(getTag(this), "Uncaught Exception: ", ex);
        defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
      }
    });
    mInstance = this;
  }

  public static synchronized MyApplication getInstance() {
    return mInstance;
  }

  public RequestQueue getRequestQueue() {
    if (mRequestQueue == null) {
      mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    return mRequestQueue;
  }

/*
  public ImageLoader getImageLoader() {
    getRequestQueue();
    if (mImageLoader == null) {
      mImageLoader = new ImageLoader(this.mRequestQueue,
          new LruBitmapCache());
    }
    return this.mImageLoader;
  }
*/

  public <T> void addToRequestQueue(Request<T> req, String tag) {
    if (req == null) {
      return;
    }
    // set the default tag if tag is empty
    req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
    getRequestQueue().add(req);
    Log.d(getTag(this), "Making volley request with tag: " + tag);
  }

  public <T> void addToRequestQueue(Request<T> req) {
    addToRequestQueue(req, TAG);
  }

  public void cancelPendingRequests(Object tag) {
    if (mRequestQueue != null) {
      mRequestQueue.cancelAll(tag);
    }
  }

  public static String getTag(Object source) {
    return TAG_PREFIX + source.getClass().getSimpleName();
  }

  public static String getTag(String tag) {
    return TAG_PREFIX + tag;
  }
}