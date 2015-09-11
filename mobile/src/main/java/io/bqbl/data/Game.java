package io.bqbl.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.bqbl.MyApplication;
import io.bqbl.data.Team.ResultType;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

/**
 * Created by sam on 7/26/2015.
 */
public class Game {
  public static final String JSON_KEY_SPORT_ID = "sport_id";
  public static final String JSON_KEY_CREATOR = "creator";
  public static final String JSON_KEY_VENUE_ID = "venue_id";
  public static final String JSON_KEY_DATE = "date";
  public static final String JSON_KEY_TEAMS = "teams";
  public static final String JSON_KEY_WOOHOOS = "woohoos";
  public static final String JSON_KEY_BOOHOOS = "boohoos";
  public static final String JSON_KEY_COMMENTS = "comments";

  protected Map<Integer, Team> userToTeam = new HashMap<>();

  public static Game create(int gameId, int sportId, int creator, String placeId, Date date, List<Team> teams, List<Integer> woohoos, List<Integer> boohoos, List<Comment> comments) {
    Game game = new Game(gameId, sportId, creator, placeId, date, teams, woohoos, boohoos, comments);
    Collections.sort(teams, new Comparator<Team>() {
      @Override
      public int compare(Team lhs, Team rhs) {
        if (lhs == null) {
          return -1;
        }
        if (rhs == null) {
          return 1;
        }
        return lhs.rank() - rhs.rank();
      }
    });
    setResultTypes(teams);

    for (Team team : teams) {
      for (Integer userId : team.users()) {
        game.userToTeam.put(userId, team);
      }
    }


    return game;
  }

  public Game(int gameId, int sportId, int creator, String placeId, Date date, List<Team> teams,
               List<Integer> woohoos, List<Integer> boohoos, List<Comment> comments) {
    this.id=gameId;
    this.sportId=sportId;
    this.creator=creator;
    this.placeId=placeId;
    this.date=date;
    this.teams=teams;
    this.woohoos=woohoos;
    this.boohoos=boohoos;
    this.comments=comments;
  }

  public static Game fromJSON(int gameId, JSONObject json) {
    List<Team> teams = new LinkedList<>();
    List<Integer> woohoos = new LinkedList<>();
    List<Integer> boohoos = new LinkedList<>();
    List<Comment> comments = new LinkedList<>();

    try {
      JSONArray teamsArray = json.getJSONArray(JSON_KEY_TEAMS);
      for (int teamId = 0; teamId < teamsArray.length(); teamId++) {
        teams.add(Team.fromJSON(gameId, teamId, teamsArray.getJSONObject(teamId)));
      }
      JSONArray woohoosArray = json.getJSONArray(JSON_KEY_WOOHOOS);
      for (int i = 0; i < woohoosArray.length(); i++) {
        woohoos.add(Integer.valueOf(woohoosArray.getString(i)));
      }
      JSONArray boohoosArray = json.getJSONArray(JSON_KEY_BOOHOOS);
      for (int i = 0; i < boohoosArray.length(); i++) {
        boohoos.add(Integer.valueOf(boohoosArray.getString(i)));
      }
      JSONArray commentsArray = json.getJSONArray(JSON_KEY_COMMENTS);
      for (int i = 0; i < commentsArray.length(); i++) {
        Comment comment = Comment.fromJSON(gameId, commentsArray.getJSONObject(i));
        if (comment != null) {
          comments.add(comment);
        }
      }
      return create(gameId,
          json.getInt(JSON_KEY_SPORT_ID),
          json.getInt(JSON_KEY_CREATOR),
          json.getString(JSON_KEY_VENUE_ID),
          new Date(json.getLong(JSON_KEY_DATE) * 1000),
          teams,
          woohoos,
          boohoos,
          comments);
    } catch (Exception e) {
      Log.e(logTag(Game.class.getSimpleName()), "", e);
    }
    return null;
  }

  public JSONObject toJSON() {
    JSONObject game = new JSONObject();
    JSONArray teams = new JSONArray();
    JSONArray woohoos = new JSONArray();
    JSONArray boohoos = new JSONArray();
    JSONArray comments = new JSONArray();
    for (Team team : teams()) {
      teams.put(team.toJSON());
    }
    for (Integer woohoo : woohoos()) {
      woohoos.put(woohoo);
    }
    for (Integer boohoo : boohoos()) {
      boohoos.put(boohoo);
    }
    for (Comment comment : comments()) {
      comments.put(comment.toJSON());
    }
    try {
      game.put(JSON_KEY_SPORT_ID, sportId())
          .put(JSON_KEY_CREATOR, creator())
          .put(JSON_KEY_VENUE_ID, placeId())
          .put(JSON_KEY_DATE, (long) (date().getTime() / 1000))
          .put(JSON_KEY_TEAMS, teams)
          .put(JSON_KEY_WOOHOOS, woohoos)
          .put(JSON_KEY_BOOHOOS, boohoos)
          .put(JSON_KEY_COMMENTS, comments);
    } catch (JSONException e) {
      return null;
    }
    return game;
  }

  public Team getTeamForUser(int user) {
    for (Team team : teams) {
      if (team.users.contains(user)) {
        return team;
      }
    }
    return null;
  }

  public static void setResultTypes(List<Team> teams) {
    if (teams.size() == 1) {
      try {
        double score = Double.valueOf(teams.get(0).score());
        if (score < 0) {
          teams.get(0).resultTypeHolder.value = ResultType.LOSS;
        } else if (score == 0) {
          teams.get(0).resultTypeHolder.value = ResultType.TIE;
        } else {
          teams.get(0).resultTypeHolder.value = ResultType.WIN;
        }
      } catch (Exception e) {
        teams.get(0).resultTypeHolder.value = ResultType.TIE;
      }
      return;
    }
    if (teams.get(0).rank()
        == teams.get(teams.size() - 1).rank()) {
      for (Team team : teams) {
        team.resultTypeHolder().value = ResultType.TIE;
      }
    } else if (teams.size() == 2) {
      teams.get(0).resultTypeHolder().value = ResultType.WIN;
      teams.get(1).resultTypeHolder().value = ResultType.LOSS;
    } else {
      for (Team team : teams) {
        ResultType resultType;
        switch (team.rank()) {
          case 1:
            resultType = ResultType.GOLD;
            break;
          case 2:
            resultType = ResultType.SILVER;
            break;
          case 3:
            resultType = ResultType.BRONZE;
            break;
          default:
            resultType = ResultType.RANK;
        }
        team.resultTypeHolder().value = resultType;
      }
    }
  }

  @Deprecated
  public static Request requestGame(final int gameId, final Listener<Game> listener) {
    return requestGame(gameId, false, listener);
  }

  public static Request requestGame(final int gameId, boolean shouldCommit, final Listener<Game> listener) {
    Game cached = CacheManager.getInstance().getGame(gameId);
    if (cached != null) {
      listener.onResult(cached);
      return null;
    }

    Request request = WebUtils.getJsonRequest(URLs.GAME_PHP + "?gameid=" + gameId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Game game = Game.fromJSON(gameId, response);
        CacheManager.getInstance().addGame(game);
        listener.onResult(game);
      }
    });

    if (shouldCommit) {
      MyApplication.getInstance().addToRequestQueue(request, "Requesting game " + gameId);
    }

    return request;
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


  public int id;

  public int sportId;

  public int creator;

  public String placeId;

  public Date date;

  public List<Team> teams;

  public List<Integer> woohoos;

  public List<Integer> boohoos;

  public List<Comment> comments;

  @Override
  public String toString() {
    return String.format("id: %d, sportId: %d, creator: %d, placeId: %s, date: %s, #teams: %d, #woohoos: %d, #boohoos: %d, #comments: %d",
        id,sportId,creator,placeId,date.toString(),teams.size(),woohoos.size(),boohoos().size(),comments.size());
  }

  public int id(){return id;}

  public int sportId(){return sportId;}

  public int creator(){return creator;}

  public String placeId(){return placeId;}

  public Date date(){return date;}

  public List<Team> teams(){return teams;}

  public List<Integer> woohoos(){return woohoos;}

  public List<Integer> boohoos(){return boohoos;}

  public List<Comment> comments(){return comments;}
}
