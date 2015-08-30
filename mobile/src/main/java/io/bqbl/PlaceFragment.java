package io.bqbl;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;

import io.bqbl.data.PlaceManager;
import io.bqbl.utils.Listener;


public class PlaceFragment extends Fragment {

  private static final String EXTRA_PLACE_ID = "place_id";
  private String mPlaceId;
  private GoogleApiClient mGoogleApiClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mGoogleApiClient = PlaceManager.getGoogleApiClient(getActivity(), new GoogleApiClient.ConnectionCallbacks() {
      @Override
      public void onConnected(Bundle bundle) {
        Log.d(MyApplication.logTag(PlaceFragment.this), "GoogleApiClient connected");
      }

      @Override
      public void onConnectionSuspended(int i) {
        Log.d(MyApplication.logTag(PlaceFragment.this), "GoogleApiClient suspended");
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  @Override
  public void onStop() {
    mGoogleApiClient.disconnect();
    super.onStop();
  }

  @Override
  public void onResume() {
    mPlaceId = getActivity().getIntent().getExtras().getString(EXTRA_PLACE_ID, null);
    if (mPlaceId != null) {
      PlaceManager.getPlace(mPlaceId, mGoogleApiClient, new Listener<Place>() {
        @Override
        public void onResult(Place place) {
          ((TextView) getView().findViewById(R.id.place_textview)).setText(place.toString());
        }
      });
    }
  }
}
