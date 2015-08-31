package io.bqbl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import io.bqbl.data.User;


public class ProfileActivity extends AppCompatActivity {
  public static final String EXTRA_USER_ID = "user_id";
  public static final String EXTRA_USER_NAME = "user_name";

  public static void startActivity(Context context, User user) {
    Intent intent = new Intent(context, ProfileActivity.class);
    intent.putExtra(EXTRA_USER_ID, user.id());
    intent.putExtra(EXTRA_USER_NAME, user.name());
    context.startActivity(intent, null);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(getIntent().getStringExtra(EXTRA_USER_NAME));

    //mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
    //android.util.Log.d(logTag("DEBUGLOG"), "toolbar null: " + (mToolbar == null));
    //setSupportActionBar(mToolbar);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_profile, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    if (id == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
