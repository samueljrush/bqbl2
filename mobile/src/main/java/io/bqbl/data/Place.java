package io.bqbl.data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.auto.value.AutoValue;

import org.json.JSONException;
import org.json.JSONObject;

import io.bqbl.MyApplication;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

/**
 * Created by sam on 8/30/2015.
 */
@AutoValue
public abstract class Place {
  public static final String JSON_KEY_NAME = "name";
  public static final String JSON_KEY_LAT = "lat";
  public static final String JSON_KEY_LNG = "lng";
  public static final String JSON_KEY_ICON_URL = "icon_url";
  public static final String JSON_KEY_PHOTO_URL = "photo_url";

  public static Place create(String id, String name, double lat, double lng, String iconUrl, String photoUrl) {
    return new AutoValue_Place.Builder()
        .id(id)
        .name(name)
        .lat(lat)
        .lng(lng)
        .iconUrl(iconUrl)
        .photoUrl(photoUrl)
        .build();
  }

  public static Place fromJSON(String id, JSONObject json) {
    try {
      return new AutoValue_Place.Builder()
          .id(id)
          .name(json.getString(JSON_KEY_NAME))
          .lat(json.getDouble(JSON_KEY_LAT))
          .lng(json.getDouble(JSON_KEY_LNG))
          .iconUrl(json.getString(JSON_KEY_ICON_URL))
          .photoUrl(json.getString(JSON_KEY_PHOTO_URL))
          .build();
    } catch (JSONException e) {
      return null;
    }
  }

  public static Request requestPlace(final String placeId, boolean commitRequest, final Listener<Place> listener) {
    Place cached = CacheManager.getInstance().getPlace(placeId);
    if (cached != null) {
      listener.onResult(cached);
      return null;
    }

    Request request = WebUtils.getRequest(URLs.PLACE_PHP + "?id=" + placeId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Place place = Place.fromJSON(placeId, response);
        CacheManager.getInstance().addPlace(place);
        listener.onResult(place);
      }
    });

    if (commitRequest) {
      MyApplication.getInstance().addToRequestQueue(request, "Fetch place " + placeId);
    }

    return request;
  }

  public abstract String id();
  public abstract String name();
  public abstract double lat();
  public abstract double lng();
  public abstract String iconUrl();
  public abstract String photoUrl();
  public abstract Builder toBuilder();

  @AutoValue.Builder
  abstract static class Builder {
    public abstract Builder id(String id);
    public abstract Builder name(String name);
    public abstract Builder lat(double lat);
    public abstract Builder lng(double lng);
    public abstract Builder iconUrl(String iconUrl);
    public abstract Builder photoUrl(String photoUrl);
    public abstract Place build();
  }
}
