package io.bqbl.data;

import com.google.auto.value.AutoValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@AutoValue
public abstract class Comment {
  public static final String JSON_KEY_USERID = "user_id";
  public static final String JSON_KEY_TEXT = "text";
  public static final String JSON_KEY_GAME_ID = "game_id";
  public static final String JSON_KEY_DATE = "date";
  public static final String JSON_KEY_COMMENT_ID = "id";

  public static Comment create(int id, int gameId, int userId, String text, Date date) {
    return new AutoValue_Comment(
        id,
        gameId,
        userId,
        text,
        date);
  }

  public static Comment fromJSON(int gameId, JSONObject json) {
    try {
      return new AutoValue_Comment(
          json.getInt(JSON_KEY_COMMENT_ID),
          gameId,
          json.getInt(JSON_KEY_USERID),
          json.getString(JSON_KEY_TEXT),
          new Date(json.getLong(JSON_KEY_DATE) * 1000));

    } catch (Exception e) {
      return null;
    }
  }

  public JSONObject toJSON() {
    JSONObject commentJSON = new JSONObject();
    try {
      commentJSON.put(JSON_KEY_COMMENT_ID, id())
          .put(JSON_KEY_USERID, userId())
          .put(JSON_KEY_GAME_ID, gameId())
          .put(JSON_KEY_TEXT, text())
          .put(JSON_KEY_DATE, (int) (date().getTime() / 1000));
    } catch (JSONException e) {
      return null;
    }
    return commentJSON;
  }

  public abstract int id();

  public abstract int gameId();

  public abstract int userId();

  public abstract String text();

  public abstract Date date();
}