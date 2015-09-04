package io.bqbl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class PlaceActivity extends AppCompatActivity {
  public static final String EXTRA_PLACE_ID = "place_id";

  public static void startActivity(Context context, String placeId) {
    Intent intent = new Intent(context, PlaceActivity.class);
    intent.putExtra(EXTRA_PLACE_ID, placeId);
    context.startActivity(intent, null);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_place);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    //mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
    //android.util.Log.d(logTag("DEBUGLOG"), "toolbar null: " + (mToolbar == null));
    //setSupportActionBar(mToolbar);
  }



  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
