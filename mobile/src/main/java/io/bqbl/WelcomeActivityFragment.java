package io.bqbl;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONObject;

import io.bqbl.utils.SharedPreferencesUtils;
import io.bqbl.utils.WebUtils;


/**
 * A placeholder fragment containing a simple view.
 */
public class WelcomeActivityFragment extends Fragment {

  private static final int ACCOUNT_SELECTION_REQUEST = 1;
  private static final String TAG = WelcomeActivityFragment.class.getSimpleName();

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
    int userid = SharedPreferencesUtils.getInt(getActivity(), R.string.pref_current_user, R.string.pref_current_user_default);
    if (userid > 0) {
      Intent intent = new Intent(getActivity(), MainActivity.class);
      startActivity(intent);
      android.util.Log.v(MyApplication.logTag(this), "user id: " + userid);
    } else {
      android.util.Log.v(MyApplication.logTag(this), "Asking for google account.");
      Intent googlePicker = AccountPicker.newChooseAccountIntent(
          null,
          null,
          new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
          false,
          "BQBLio uses your Google account for authentication",
          null,
          null,
          null);
      startActivityForResult(googlePicker, ACCOUNT_SELECTION_REQUEST);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    android.util.Log.v(MyApplication.logTag(this), "onActivityResult");
    if (requestCode == ACCOUNT_SELECTION_REQUEST && resultCode == Activity.RESULT_OK) {
      Log.d(MyApplication.logTag(this), "onActivityResult");
      final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
      final MyApplication myApp = (MyApplication) getActivity().getApplicationContext();
      Request request = WebUtils.getRequest("http://bqbl.io/io/json/userlookup.php?email=" + accountName, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          try {
            int userid = response.getInt("user_id");
            android.util.Log.v(MyApplication.logTag(WelcomeActivityFragment.this), String.format("retrieved email: %s, id: %d", accountName, userid));
            if (userid > 0) {
              SharedPreferencesUtils.setCurrentUser(myApp, userid);
              Intent intent = new Intent(getActivity(), MainActivity.class);
              startActivity(intent);
            } else {
              Intent intent = new Intent(getActivity(), CreateAccountActivity.class);
              intent.putExtra(CreateAccountActivity.EMAIL_EXTRA, accountName);
              startActivity(intent);
            }
          } catch (Exception e) {
            android.util.Log.e(MyApplication.logTag(WelcomeActivityFragment.this), "Exception thrown", e);
          }
        }
      });
      myApp.addToRequestQueue(request);
    }
  }
}
