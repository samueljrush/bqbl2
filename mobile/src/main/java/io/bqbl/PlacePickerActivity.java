package io.bqbl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static io.bqbl.MyApplication.logTag;


public class PlacePickerActivity extends Activity {

  private static final int PLACE_PICKER_REQUEST = 1   ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_place_picker);
    try {
      Intent intent = new PlacePicker.IntentBuilder().build(this);
      // Start the intent by requesting a result,
      // identified by a request code.
      startActivityForResult(intent, PLACE_PICKER_REQUEST);
    } catch (Exception e) {
      Log.e(logTag(this), "Error getting PlacePicker intent" , e);
    }
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data) {
    if (requestCode == PLACE_PICKER_REQUEST
        && resultCode == Activity.RESULT_OK) {

      // The user has selected a place. Extract the name and address.
      final Place place = PlacePicker.getPlace(data, this);

      final String placeId = place.getId();
      String attributionHtml = PlacePicker.getAttributions(data);
      CharSequence attributions = attributionHtml == null ? "" : Html.fromHtml(attributionHtml);
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
    finish();
  }
}
