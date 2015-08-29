package io.bqbl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;

import io.bqbl.data.PlaceManager;
import io.bqbl.utils.Listener;

import static io.bqbl.MyApplication.getTag;


public class PlaceActivity extends Activity {

  private static final String EXTRA_PLACE_ID = "place_id";
  private String mPlaceId;
  private GoogleApiClient mGoogleApiClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_place);
    mGoogleApiClient = PlaceManager.getGoogleApiClient(this, new GoogleApiClient.ConnectionCallbacks() {
      @Override
      public void onConnected(Bundle bundle) {
        Log.d(getTag(PlaceActivity.this), "GoogleApiClient connected");
      }

      @Override
      public void onConnectionSuspended(int i) {
        Log.d(getTag(PlaceActivity.this), "GoogleApiClient suspended");
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_place, menu);
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

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    mGoogleApiClient.disconnect();
    super.onStop();
  }

  @Override
  public void onResume() {
    mPlaceId = getIntent().getExtras().getString(EXTRA_PLACE_ID, null);
    if (mPlaceId != null) {
      PlaceManager.getPlace(mPlaceId, mGoogleApiClient, new Listener<Place>() {
        @Override
        public void onResult(Place place) {
          ((TextView) findViewById(R.id.place_textview)).setText(place.toString());
        }
      });
    }
  }
}
