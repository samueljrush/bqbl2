package io.bqbl;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONObject;

import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;


/**
 * A placeholder fragment containing a simple view.
 */
public class WelcomeActivityFragment extends Fragment {

  private static final int ACCOUNT_SELECTION_REQUEST = 1;
  private static final String TAG = WelcomeActivityFragment.class.getSimpleName();
  private static final long SPLASH_DELAY_MILLIS = 2000;

  private Handler mHandler = new Handler();
  
  public WelcomeActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_welcome, container, false);
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(logTag(this), "Getting user id...");
    int userid = MyApplication.getCurrentUser(getActivity());
    Log.d(logTag(this), "Got user id");
    if (userid > 0) {
      Intent intent = new Intent(getActivity(), MainActivity.class);
      startActivity(intent);
      android.util.Log.v(logTag(this), "user id: " + userid);
      getActivity().finish();
    } else {
      android.util.Log.v(logTag(this), "Asking for google account.");
      final Intent googlePicker = AccountPicker.newChooseAccountIntent(
          null,
          null,
          new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
          false,
          "BQBLio uses your Google account for authentication",
          null,
          null,
          null);
      mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          startActivityForResult(googlePicker, ACCOUNT_SELECTION_REQUEST);
        }
      }, SPLASH_DELAY_MILLIS);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    android.util.Log.v(logTag(this), "onActivityResult");
    if (requestCode == ACCOUNT_SELECTION_REQUEST && resultCode == Activity.RESULT_OK) {
      Log.d(logTag(this), "onActivityResult");
      final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
      final MyApplication myApp = (MyApplication) getActivity().getApplicationContext();
      Request request = WebUtils.getRequest("http://bqbl.io/io/json/userlookup.php?email=" + accountName, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          try {
            int userid = response.getInt("user_id");
            android.util.Log.v(logTag(WelcomeActivityFragment.this), String.format("retrieved email: %s, id: %d", accountName, userid));
            if (userid > 0) {
              MyApplication.setCurrentUser(myApp, userid);
              Intent intent = new Intent(getActivity(), MainActivity.class);
              startActivity(intent);
            } else {
              Intent intent = new Intent(getActivity(), CreateAccountActivity.class);
              intent.putExtra(CreateAccountActivity.EMAIL_EXTRA, accountName);
              startActivity(intent);
            }
          } catch (Exception e) {
            android.util.Log.e(logTag(WelcomeActivityFragment.this), "Exception thrown", e);
          }
          getActivity().finish();
        }
      });
      myApp.addToRequestQueue(request);
    }
  }
}
