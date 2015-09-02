package io.bqbl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import io.bqbl.data.User;
import io.bqbl.utils.WebUtils;


public class CreateAccountActivity extends Activity {

  public static final String EMAIL_EXTRA = "email";


  private static final String TAG = CreateAccountActivity.class.getSimpleName();

  private String mEmail;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mEmail = getIntent().getExtras().getString(EMAIL_EXTRA);
    ((TextView) findViewById(R.id.create_activity_email_edit)).setText(mEmail);
    findViewById(R.id.create_activity_submit).setOnClickListener(mSubmitListener);
  }

  private View.OnClickListener mSubmitListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      String firstName = ((TextView) findViewById(R.id.create_activity_first_name_edit)).getText().toString();
      String lastName = ((TextView) findViewById(R.id.create_activity_last_name_edit)).getText().toString();
      String phoneNumber = ((TextView) findViewById(R.id.create_activity_phone_number_edit)).getText().toString();
      String penisSize = ((TextView) findViewById(R.id.create_activity_penis_size_edit)).getText().toString();
      if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(penisSize)) {
        new AlertDialog.Builder(CreateAccountActivity.this).setMessage("All fields are required!").create().show();
      } else {
        JSONObject post = User.create(-1, mEmail, firstName, lastName, phoneNumber, Double.parseDouble(penisSize)).toJSON();

        final ProgressDialog progress = new ProgressDialog(CreateAccountActivity.this);
        progress.setTitle("Creating account...");
        progress.show();

        final MyApplication myApp = (MyApplication) getApplicationContext();

        Request request = WebUtils.postJsonRequest("http://bqbl.io/io/json/createaccount.php", post, new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
            Log.e(TAG, "onResponse!");
            progress.dismiss();
            try {
              int userid = response.getInt("user_id");
              if (userid > 0) {
                MyApplication.setCurrentUser(myApp, userid);
                Intent intent = new Intent(CreateAccountActivity.this, FeedFragment.class);
                startActivity(intent);
              } else {
                // TODO: handle
              }
            } catch (Exception e) {
              android.util.Log.e(TAG, "Exception thrown", e);
            }
          }
        });
        myApp.addToRequestQueue(request);
      }
    }
  };
}
