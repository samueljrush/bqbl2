package io.bqbl.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.bqbl.utils.Holder;

public class Team {
  public static final String JSON_KEY_RANK = "rank";
  public static final String JSON_KEY_TIE = "tie";
  public static final String JSON_KEY_SCORE = "score";
  public static final String JSON_KEY_USERS = "users";

  public static Team create(int gameId, int teamId, int rank, boolean tie, String score, List<Integer> users, Holder<ResultType> resultTypeHolder) {
    return new Team(gameId, teamId, rank, tie, score, users, resultTypeHolder);
  }

  public Team(int gameId, int teamId, int rank, boolean tie, String score, List<Integer> users, Holder<ResultType> resultTypeHolder) {
    this.gameId = gameId;
    this.teamId = teamId;
    this.rank = rank;
    this.tie = tie;
    this.score = score;
    this.users = users;
    this.resultTypeHolder = resultTypeHolder;
  }

  public Team() {
    this(-1, -1, -1, false, "", new ArrayList<Integer>(), new Holder<ResultType>(ResultType.RANK));
  }

  public static Team fromJSON(int gameId, int teamId, JSONObject json) {
    List<Integer> users = new LinkedList<>();
    try {
      JSONArray usersArray = json.getJSONArray(JSON_KEY_USERS);
      for (int i = 0; i < usersArray.length(); i++) {
        users.add(Integer.valueOf(usersArray.getString(i)));
      }

      return create(
          gameId,
          teamId,
          json.getInt(JSON_KEY_RANK),
          json.getInt(JSON_KEY_TIE) == 1,
          json.getString(JSON_KEY_SCORE),
          users,
          new Holder<>(ResultType.RANK));
    } catch (Exception e) {
      return null;
    }
  }

  public JSONObject toJSON() {
    JSONObject teamJSON = new JSONObject();
    JSONArray usersJSON = new JSONArray();
    for (int userId : users) {
      usersJSON.put(userId);
    }
    try {
      teamJSON.put(JSON_KEY_RANK, rank)
          .put(JSON_KEY_TIE, tie ? 1 : 0)
          .put(JSON_KEY_SCORE, score)
          .put(JSON_KEY_USERS, usersJSON);
    } catch (JSONException e) {
      return null;
    }
    return teamJSON;
  }

  public int teamId;
  public int gameId;
  public int rank;
  public boolean tie;
  public String score;
  public List<Integer> users;
  public Holder<ResultType> resultTypeHolder;

  public int teamId(){return teamId;}
  public int gameId(){return gameId;}
  public int rank(){return rank;}
  public boolean tie(){return tie;}
  public String score(){return score;}
  public List<Integer> users(){return users;}
  public Holder<ResultType> resultTypeHolder(){return resultTypeHolder;}


  public enum ResultType {
    WIN,
    LOSS,
    TIE,
    GOLD,
    SILVER,
    BRONZE,
    RANK
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamId, gameId,rank,tie,score,users,resultTypeHolder.value);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Team)) {
      return false;
    }

    Team t = (Team) o;
    return teamId==t.teamId
        && gameId==t.gameId
        && rank==t.rank
        && tie==t.tie
        && score==t.score
        && users==t.users
        && resultTypeHolder.value==t.resultTypeHolder.value;
  }
}