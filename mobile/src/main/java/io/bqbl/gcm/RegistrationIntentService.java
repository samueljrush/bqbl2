package io.bqbl.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import java.io.IOException;

import io.bqbl.MyApplication;
import io.bqbl.R;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

public class RegistrationIntentService extends IntentService {

  private static final String TAG = "RegIntentService";
  private static final String[] TOPICS = {"global"};

  public RegistrationIntentService() {
    super(TAG);
    //Log.d(logTag(TAG), "Service Constructed");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    //Log.d(logTag(TAG), "Service Created");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    //Log.d(logTag(TAG), "Service Handling intent");
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    try {
      // [START register_for_gcm]
      // Initially this call goes out to the network to retrieve the token, subsequent calls
      // are local.
      // [START get_token]
      InstanceID instanceID = InstanceID.getInstance(this);
      String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
          GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

      // [END get_token]
      Log.i(logTag(TAG), "GCM Registration Token: " + token);

      // TODO: Implement this method to send any registration to your app's servers.
      sendRegistrationToServer(token);

      // Subscribe to topic channels
      subscribeTopics(token);

      // You should store a boolean that indicates whether the generated token has been
      // sent to your server. If the boolean is false, send the token to your server,
      // otherwise your server should have already received the token.
      sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
      // [END register_for_gcm]
    } catch (Exception e) {
      //Log.d(logTag(TAG), "Failed to complete token refresh", e);
      // If an exception happens while fetching the new token or updating our registration data
      // on a third-party server, this ensures that we'll attempt the update at a later time.
      sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
    }
    // Notify UI that registration has completed, so the progress indicator can be hidden.
    Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
  }

  /**
   * Persist registration to third-party servers.
   * <p/>
   * Modify this method to associate the user's GCM registration token with any server-side account
   * maintained by your application.
   *
   * @param token The new token.
   */
  private void sendRegistrationToServer(String token) {
    MyApplication app = MyApplication.getInstance();
    app.addToRequestQueue(WebUtils.getJsonRequest(URLs.getSetGcmFormatUrl(app.getCurrentUser(), token), new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {

      }
    }), "Sending GCM token");
  }

  /**
   * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
   *
   * @param token GCM token
   * @throws IOException if unable to reach the GCM PubSub service
   */
  // [START subscribe_topics]
  private void subscribeTopics(String token) throws IOException {
    for (String topic : TOPICS) {
      GcmPubSub pubSub = GcmPubSub.getInstance(this);
      pubSub.subscribe(token, "/topics/" + topic, null);
    }
  }
  // [END subscribe_topics]

}