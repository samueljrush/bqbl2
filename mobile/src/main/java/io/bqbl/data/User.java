package io.bqbl.data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.auto.value.AutoValue;

import org.json.JSONException;
import org.json.JSONObject;

import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

/**
 * Created by sam on 7/26/2015.
 */
@AutoValue
public abstract class User {
  public static final String JSON_KEY_FIRST_NAME = "first_name";
  public static final String JSON_KEY_PHONE_NUMBER = "phone";
  public static final String JSON_KEY_LAST_NAME = "last_name";
  public static final String JSON_KEY_PENIS_SIZE = "penis_size";
  public static final String JSON_KEY_EMAIL = "email";
  private static final String JSON_KEY_USERID = "userid";

  public static User create(int id, String email, String first, String last, String phone, double penis) {
    return new AutoValue_User.Builder().id(id).email(email).first(first).last(last).phone(phone).penis(penis).build();
    // (or just AutoValue_BQBLObjects_Animal if this is not nested)
  }

  public static User fromJSON(JSONObject json) {
    try {
      return new AutoValue_User.Builder()
          .id(json.getInt(JSON_KEY_USERID))
          .email(json.getString(JSON_KEY_EMAIL))
          .first(json.getString(JSON_KEY_FIRST_NAME))
          .last(json.getString(JSON_KEY_LAST_NAME))
          .phone(json.getString(JSON_KEY_PHONE_NUMBER))
          .penis(json.getDouble(JSON_KEY_PENIS_SIZE))
          .build();
    } catch (JSONException e) {
      return null;
    }
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    try {
      json.put(JSON_KEY_USERID, id());
      json.put(JSON_KEY_EMAIL, email());
      json.put(JSON_KEY_FIRST_NAME, first());
      json.put(JSON_KEY_LAST_NAME, last());
      json.put(JSON_KEY_PHONE_NUMBER, phone());
      json.put(JSON_KEY_PENIS_SIZE, penis());
    } catch (JSONException e) {
      // TODO: handle error
    }
    return json;
  }
  
  public String getPhotoUri() {
    return String.format(URLs.USER_PHOTO_FORMAT, id());
  }
  
  public static Request requestUser(final int userId, final Listener<User> listener) {
    User cached = CacheManager.getInstance().getUser(userId);
    if (cached != null) {
      listener.onResult(cached);
      return null;
    }

    return WebUtils.getRequest(URLs.SETTINGS_PHP + "?userid=" + userId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        User user = User.fromJSON(response);
        CacheManager.getInstance().addUser(user);
        listener.onResult(user);
      }
    });
  }

  public abstract int id();
  public abstract String email();
  public abstract String first();
  public abstract String last();
  public abstract String phone();
  public abstract double penis();
  public abstract Builder toBuilder();

  @AutoValue.Builder
  abstract static class Builder {
    public abstract Builder id(int id);
    public abstract Builder email(String email);
    public abstract Builder first(String first);
    public abstract Builder last(String last);
    public abstract Builder phone(String phone);
    public abstract Builder penis(double penis);
    public abstract User build();
    }
  }
