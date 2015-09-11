package io.bqbl;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONObject;

import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;


public class WelcomeActivity extends AppCompatActivity {
  private static final int ACCOUNT_SELECTION_REQUEST = 1;
  private static final long SPLASH_DELAY_MILLIS = 2000;
  private Handler mHandler = new Handler();
  private boolean mHasPosted = false;

  private Runnable mStartPickerRunnable = new Runnable() {
    @Override
    public void run() {
      final Intent googlePicker = AccountPicker.newChooseAccountIntent(
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
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
  }

  @Override
  public void onStart() {
    super.onStart();
    //Log.d(logTag(this), "Getting user id...");
    int userid = MyApplication.getCurrentUser();
    //Log.d(logTag(this), "Got user id");
    if (userid > 0) {
      Intent intent = new Intent(this, MainActivity.class);
      startActivity(intent);
      android.util.Log.v(logTag(this), "user id: " + userid);
      finish();
    } else {
      if (mHasPosted) {
        return;  // don't refresh dialog
      }
      mHasPosted = true;
      android.util.Log.v(logTag(this), "Asking for google account.");
      mHandler.postDelayed(mStartPickerRunnable, SPLASH_DELAY_MILLIS);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    android.util.Log.v(logTag(this), "onActivityResult");
    if (requestCode == ACCOUNT_SELECTION_REQUEST && resultCode == Activity.RESULT_OK) {
      mHandler.removeCallbacks(mStartPickerRunnable);
      //Log.d(logTag(this), "onActivityResult");
      final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
      Request request = WebUtils.getJsonRequest("http://bqbl.io/io/json/userlookup.php?email=" + accountName, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          try {
            int userid = response.getInt("user_id");
            android.util.Log.v(logTag(this), String.format("retrieved email: %s, id: %d", accountName, userid));
            if (userid > 0) {
              MyApplication.setCurrentUser(WelcomeActivity.this, userid);
              Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
              startActivity(intent);
            } else {
              Intent intent = new Intent(WelcomeActivity.this, CreateAccountActivity.class);
              intent.putExtra(CreateAccountActivity.EMAIL_EXTRA, accountName);
              startActivity(intent);
            }
          } catch (Exception e) {
            android.util.Log.e(logTag(WelcomeActivity.this), "Exception thrown", e);
          }
          finish();
        }
      });
      MyApplication.getInstance().addToRequestQueue(request);
    }
  }
}
