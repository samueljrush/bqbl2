package io.bqbl;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.TextView;

import io.bqbl.data.Place;
import io.bqbl.utils.Listener;


public class PlaceFragment extends Fragment {

  private static final String EXTRA_PLACE_ID = "place_id";
  private String mPlaceId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  @Override
  public void onResume() {
    mPlaceId = getActivity().getIntent().getExtras().getString(EXTRA_PLACE_ID, null);
    if (mPlaceId != null) {
      Place.requestPlace(mPlaceId, true, new Listener<Place>() {
        @Override
        public void onResult(Place place) {
          ((TextView) getView().findViewById(R.id.place_textview)).setText(place.toString());
        }
      });
    }
  }
}
