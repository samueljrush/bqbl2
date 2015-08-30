package io.bqbl.data;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import io.bqbl.utils.Listener;

import static io.bqbl.MyApplication.logTag;

/**
 * Created by sam on 8/2/2015.
 */
public final class PlaceManager {
  private PlaceManager(){}

  public static GoogleApiClient getGoogleApiClient(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
    return new GoogleApiClient.Builder(context)
        .addApi(Places.GEO_DATA_API)
        .addApi(Places.PLACE_DETECTION_API)
        .addConnectionCallbacks(connectionCallbacks)
        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
          @Override
          public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(logTag(PlaceManager.class.getSimpleName()), connectionResult.toString());
          }
        })
        .build();
  }

  public static void getPlace(final String placeId, GoogleApiClient googleApiClient, final Listener<Place> listener) {
    Log.d(logTag("PlaceManager"), "Getting place: " + placeId);
    Place cached = CacheManager.getInstance().getPlace(placeId);
    if (cached != null) {
      listener.onResult(cached);
      return;
    }

    // TODO: write logic to get venue details from google maps
    Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
        .setResultCallback(new ResultCallback<PlaceBuffer>() {
          @Override
          public void onResult(PlaceBuffer places) {
            Log.d(logTag("PlaceManager"), "Place status: " + places.getStatus());
            if (places.getStatus().isSuccess()) {
              final Place place = places.get(0);
              CacheManager.getInstance().addPlace(place);
              listener.onResult(place);
            }
            places.release();
          }
        });
  }
}
