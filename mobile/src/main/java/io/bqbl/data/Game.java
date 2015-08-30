package io.bqbl.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.auto.value.AutoValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

/**
 * Created by sam on 7/26/2015.
 */
@AutoValue
public abstract class Game {
  public static final String JSON_KEY_SPORT_ID = "sport_id";
  public static final String JSON_KEY_CREATOR = "creator";
  public static final String JSON_KEY_VENUE_ID = "venue_id";
  public static final String JSON_KEY_DATE = "date";
  public static final String JSON_KEY_TEAMS = "teams";

  protected Map<Integer, Team> userToTeam = new HashMap<>();

  public static Game create(int gameId, int sportId, int creator, String placeId, String date, Collection<Team> teams) {
    Game game = new AutoValue_Game.Builder()
        .id(gameId)
        .sportId(sportId)
        .creator(creator)
        .placeId(placeId)
        .date(date)
        .teams(teams)
        .build();

    for (Team team : teams) {
      for(Integer userId : team.users()) {
        game.userToTeam.put(userId, team);
      }
    }

    return game;
  }

  public static Game fromJSON(int gameId, JSONObject json) {
    List<Team> teams = new LinkedList<>();
    try {
      JSONArray teamsArray = json.getJSONArray(JSON_KEY_TEAMS);
      for (int teamId = 0; teamId < teamsArray.length(); teamId++) {
        teams.add(Team.fromJSON(gameId, teamId, teamsArray.getJSONObject(teamId)));
      }
      return create(gameId,
          json.getInt(JSON_KEY_SPORT_ID),
          json.getInt(JSON_KEY_CREATOR),
          json.getString(JSON_KEY_VENUE_ID),
          json.getString(JSON_KEY_DATE),
          teams);
    } catch (Exception e) {
      Log.e(logTag(Game.class.getSimpleName()), "", e);
    }
    return null;
  }

  public JSONObject toJSON() {
    JSONObject game = new JSONObject();
    JSONArray teams = new JSONArray();
    for (Team team : teams()) {
      teams.put(team.toJSON());
    }
    try {
      game.put(JSON_KEY_SPORT_ID, sportId())
          .put(JSON_KEY_CREATOR, creator())
          .put(JSON_KEY_VENUE_ID, placeId())
          .put(JSON_KEY_DATE, date())
          .put(JSON_KEY_TEAMS, teams);
    } catch (JSONException e) {
      return null;
    }
    return game;
  }

  public static Request requestGame(final int gameId, final Listener<Game> listener) {
    Game cached = CacheManager.getInstance().getGame(gameId);
    if (cached != null) {
      listener.onResult(cached);
      return null;
    }

    return WebUtils.getRequest(URLs.GAME_PHP + "?gameid=" + gameId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Game game = Game.fromJSON(gameId, response);
        CacheManager.getInstance().addGame(game);
        listener.onResult(game);
      }
    });
  }

  public int rank(int userId) {
    Team team = userToTeam.get(userId);
    if (team != null) {
      return team.rank();
    } else {
      return -1;
    }
  }

  public boolean tie(int userId) {
    Team team = userToTeam.get(userId);
    if (team != null) {
      return team.tie();
    } else {
      return false;
    }
  }

  public abstract int id();
  public abstract int sportId();
  public abstract int creator();
  public abstract String placeId();
  public abstract String date();
  public abstract Collection<Team> teams();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  abstract static class Builder {
    public abstract Builder id(int id);
    public abstract Builder sportId(int id);
    public abstract Builder creator(int id);
    public abstract Builder placeId(String placeId);
    public abstract Builder date(String date);
    public abstract Builder teams(Collection<Team> teams);
    public abstract Game build();
  }
}
