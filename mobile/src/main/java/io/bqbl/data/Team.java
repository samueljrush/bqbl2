package io.bqbl.data;

import com.google.auto.value.AutoValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import io.bqbl.utils.Holder;

@AutoValue
public abstract class Team {
  public static final String JSON_KEY_RANK = "rank";
  public static final String JSON_KEY_TIE = "tie";
  public static final String JSON_KEY_SCORE = "score";
  public static final String JSON_KEY_USERS = "users";

  public static Team create(int gameId, int teamId, int rank, boolean tie, String score, List<Integer> users, Holder<ResultType> resultTypeHolder) {
    return new AutoValue_Team.Builder()
        .gameId(gameId)
        .teamId(teamId)
        .rank(rank)
        .tie(tie)
        .score(score)
        .users(users)
        .resultTypeHolder(resultTypeHolder)
        .build();
  }

  public static Team fromJSON(int gameId, int teamId, JSONObject json) {
    List<Integer> users = new LinkedList<>();
    try {
      JSONArray usersArray = json.getJSONArray(JSON_KEY_USERS);
      for (int i = 0; i < usersArray.length(); i++) {
        users.add(Integer.valueOf(usersArray.getString(i)));
      }
      return new AutoValue_Team.Builder()
          .gameId(gameId)
          .teamId(teamId)
          .rank(json.getInt(JSON_KEY_RANK))
          .tie(json.getInt(JSON_KEY_TIE) == 1)
          .score(json.getString(JSON_KEY_SCORE))
          .users(users)
          .resultTypeHolder(new Holder<>(ResultType.RANK))
          .build();
    } catch (Exception e) {
      return null;
    }
  }

  public JSONObject toJSON() {
    JSONObject teamJSON = new JSONObject();
    JSONArray usersJSON = new JSONArray();
    for (int userId : users()) {
      usersJSON.put(userId);
    }
    try {
      teamJSON.put(JSON_KEY_RANK, rank())
          .put(JSON_KEY_TIE, tie() ? 1 : 0)
          .put(JSON_KEY_SCORE, score())
          .put(JSON_KEY_USERS, usersJSON);
    } catch (JSONException e) {
      return null;
    }
    return teamJSON;
  }

  public abstract int gameId();
  public abstract int teamId();
  public abstract int rank();
  public abstract boolean tie();
  public abstract String score();
  public abstract List<Integer> users();
  public abstract Holder<ResultType> resultTypeHolder();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  abstract static class Builder {
    public abstract Builder gameId(int id);
    public abstract Builder teamId(int id);
    public abstract Builder rank(int rank);
    public abstract Builder tie(boolean tie);
    public abstract Builder score(String score);
    public abstract Builder users(List<Integer> users);
    public abstract Builder resultTypeHolder(Holder<ResultType> resultTypeHolder);
    public abstract Team build();
  }

  public enum ResultType {
    WIN,
    LOSS,
    TIE,
    GOLD,
    SILVER,
    BRONZE,
    RANK
  }
}