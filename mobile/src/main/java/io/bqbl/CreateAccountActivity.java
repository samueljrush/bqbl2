package io.bqbl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.bqbl.data.User;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;


public class CreateAccountActivity extends AppCompatActivity {
  public static final String EMAIL_EXTRA = "email";
  static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final String JSON_KEY_BASE_64 = "base64";
  private static final String JSON_KEY_USER_ID = "user_id";

  private Toolbar mToolbar;
  private TextView mLastNameView;
  private TextView mFirstNameView;
  private TextView mPhoneNumberView;

  private static final String TAG = CreateAccountActivity.class.getSimpleName();

  private String mEmail;
  String mCurrentPhotoPath;
  private ImageView mPhotoView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);
    mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(mToolbar);
    mToolbar.setTitle("Create an Account");
    mLastNameView = (TextView) findViewById(R.id.create_activity_last_name_edit);
    mFirstNameView = (TextView) findViewById(R.id.create_activity_first_name_edit);
    mPhoneNumberView = (TextView) findViewById(R.id.create_activity_phone_number_edit);

    try {
      TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
      String phoneNumber = tMgr.getLine1Number();
      if (phoneNumber != null) {
        mPhoneNumberView.setText(phoneNumber.length() == 11 ? phoneNumber.substring(1) : phoneNumber);
      }
    } catch (Exception e) {

    }
    mEmail = getIntent().getExtras().getString(EMAIL_EXTRA);
    ((TextView) findViewById(R.id.create_activity_email_edit)).setText(mEmail);
    findViewById(R.id.create_activity_submit).setOnClickListener(mSubmitListener);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mPhotoView = (ImageView) findViewById(R.id.profile_pic);
    mPhotoView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dispatchTakePictureIntent();
      }
    });
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      Log.d(logTag(this), String.format("Bitmap bytes: %d", imageBitmap.getByteCount()));
      new AsyncTask<Bitmap, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
          Bitmap thumb = ThumbnailUtils.extractThumbnail(params[0], 300, 300, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
          return thumb;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
          Log.d(logTag(this), String.format("Bitmap of size %d ", bitmap.getByteCount()));
          mPhotoView.setImageBitmap(bitmap);
        }
      }.execute(imageBitmap);

    }
  }

  public static void sendBitmapToServer(Bitmap bitmap, int userId) {
    try {
      String base64 = encodeTobase64(bitmap);
      JSONObject post = new JSONObject();
      post.put(JSON_KEY_USER_ID, userId);
      post.put(JSON_KEY_BASE_64, base64);
      MyApplication.getInstance().addToRequestQueue(WebUtils.postJsonRequest(URLs.UPLOAD_PROFILE, post, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
        }
      }), "Upload profile picture");
    } catch (Exception e) {
      Log.e(logTag(CreateAccountActivity.class.getSimpleName()), "Exception uploading image", e);
    }
  }

  public static String encodeTobase64(Bitmap image)
  {
    Bitmap immagex=image;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] b = baos.toByteArray();
    String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

    return imageEncoded;
  }

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  private View.OnClickListener mSubmitListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      String firstName = mFirstNameView.getText().toString();
      String lastName = mLastNameView.getText().toString();
      String phoneNumber = mPhoneNumberView.getText().toString();

      if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phoneNumber)) {
        new AlertDialog.Builder(CreateAccountActivity.this).setMessage("All fields are required!").create().show();
      } else {
        JSONObject post = User.create(-1, mEmail, firstName, lastName, phoneNumber).toJSON();

        final ProgressDialog progress = new ProgressDialog(CreateAccountActivity.this);
        progress.setTitle("Creating account...");
        progress.show();

        final MyApplication myApp = (MyApplication) getApplicationContext();

        Request request = WebUtils.postJsonRequest("http://bqbl.io/io/json/createaccount.php", post, new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
            Log.e(TAG, "onResponse: " + response.toString());
            progress.dismiss();
            try {
              final int userid = response.getInt("user_id");
              if (userid > 0) {
                MyApplication.setCurrentUser(myApp, userid);
                new AsyncTask<Bitmap, Void, Void>() {
                  @Override
                  protected Void doInBackground(Bitmap... params) {
                    sendBitmapToServer(params[0], userid);
                    return (Void) null;
                  }
                }.execute(((BitmapDrawable)mPhotoView.getDrawable()).getBitmap());
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
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

  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      // Calculate ratios of height and width to requested height and width
      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);

      // Choose the smallest ratio as inSampleSize value, this will guarantee
      // a final image with both dimensions larger than or equal to the
      // requested height and width.
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }
}
