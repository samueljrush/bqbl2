package io.bqbl;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import io.bqbl.utils.SharedPreferencesUtils;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

public class MyApplication extends Application {
  public static final String TAG = MyApplication.class
      .getSimpleName();
  private static final String TAG_PREFIX = "BQBLio | ";
  public static final GoogleApiClient.ConnectionCallbacks CONNECTION_CALLBACKS = new GoogleApiClient.ConnectionCallbacks() {
    @Override
    public void onConnected(Bundle bundle) {
      Log.v(logTag("MyApplication"), "GoogleApiClient connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
      Log.v(logTag("MyApplication"), "GoogleApiClient suspended");
    }
  };
  private static int sCurrentUser = -1;

  private RequestQueue mRequestQueue;
  private ImageLoader mImageLoader;

  private static MyApplication mInstance;

  private static Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

  public static int getCurrentUser(Context context) {
    if (sCurrentUser > 0) {
      return sCurrentUser;
    }
    sCurrentUser = SharedPreferencesUtils.getInt(context, R.string.pref_current_user, R.string.pref_current_user_default);
    return sCurrentUser;
  }

  public static boolean setCurrentUser(Context context, int user) {
    sCurrentUser = user;
    getInstance().getDeviceToken();
    return SharedPreferencesUtils.putInt(context, R.string.pref_current_user, user);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(logTag(this), "Uncaught Exception: ", ex);
        defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
      }
    });
    getDeviceToken();
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
    //Log.d(logTag(this), "Making volley request with tag: " + tag);
  }

  public <T> void addToRequestQueue(Request<T> req) {
    addToRequestQueue(req, TAG);
  }

  public void cancelPendingRequests(Object tag) {
    if (mRequestQueue != null) {
      mRequestQueue.cancelAll(tag);
    }
  }

  public static String logTag(Object source) {
    return TAG_PREFIX + source.getClass().getSimpleName();
  }

  public static String logTag(String tag) {
    return TAG_PREFIX + tag;
  }

  public static int getCurrentUser() {
    return getCurrentUser(getInstance());
  }

  public void getDeviceToken() {
    new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... params) {
        GoogleCloudMessaging gcm =
            GoogleCloudMessaging.getInstance(getApplicationContext());
        try {
          String deviceToken = gcm.register("783799732849");
          addToRequestQueue(WebUtils.getRequest(URLs.getSetGcmFormatUrl(getCurrentUser(), deviceToken) , null), "Sending GCM token");
          Log.i(logTag("GCM"), "Device token : " + deviceToken);
          // update user profile document
        } catch (Exception e) {
          Log.e(logTag(this), "Error getting token", e);
        }

        return null;
      }
    }.execute((Void) null);
  }
}