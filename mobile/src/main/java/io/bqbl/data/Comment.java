package io.bqbl.data;

import com.google.auto.value.AutoValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

@AutoValue
public abstract class Comment {
  public static final String JSON_KEY_USERID = "userid";
  public static final String JSON_KEY_TEXT = "text";
  public static final String JSON_KEY_DATE = "date";

  public static Comment create(int gameId, int userId, String text, String date) {
    return new AutoValue_Team.Builder()
        .gameId(gameId)
        .userId(userId)
        .text(text)
        .date(date)
        .build();
  }

  public static Team fromJSON(int gameId, JSONObject json) {
    try {
      return new AutoValue_Comment.Builder()
          .gameId(gameId)
          .userId(json.getInt(JSON_KEY_USERID)
          .text(json.getString(JSON_KEY_TEXT))
          .date(json.getString(JSON_KEY_DATE)),
          .build();
    } catch (Exception e) {
      return null;
    }
  }

  public JSONObject toJSON() {
    JSONObject commentJSON = new JSONObject();
    try {
      commentJSON.put(JSON_KEY_USERID, userId())
          .put(JSON_KEY_TEXT, text())
          .put(JSON_KEY_DATE, date());
    } catch (JSONException e) {
      return null;
    }
    return commentJSON;
  }

  public abstract int gameId();
  public abstract int userId();
  public abstract String text();
  public abstract String date();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  abstract static class Builder {
    public abstract Builder gameId(int id);
    public abstract Builder userId(int id);
    public abstract Builder text(String text);
    public abstract Builder date(String date);
    public abstract Team build();
  }

}
