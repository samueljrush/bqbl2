package io.bqbl;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.bqbl.data.Sports;
import io.bqbl.data.Sports.Sport;
import io.bqbl.data.User;
import io.bqbl.utils.GameAdapter;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

public class ProfileFragment extends Fragment {

  private int mUserId;
  private GameAdapter mGameAdapter;
  private List<GameInfo> mGameInfos;
  private ImageView mFilteredSportView;
  private Sport mFilteredSport;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUserId = getActivity().getIntent().getIntExtra(ProfileActivity.EXTRA_USER_ID, -1);
    mGameAdapter = new GameAdapter(getActivity(), Collections.<Integer>emptyList());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  public void onResume() {
    super.onResume();
    final LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.profile_recycler);
    recyclerView.setHasFixedSize(false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(mGameAdapter);
    User.requestUser(mUserId, true, new Listener<User>() {
      @Override
      public void onResult(User user) {
        ((TextView) getView().findViewById(R.id.name_title)).setText(user.name());
      }
    });

    WebUtils.setImageRemoteUri((ImageView) getView().findViewById(R.id.profile_pic), URLs.getUserPhotoUrl(mUserId));

    Request request = WebUtils.getRequest(URLs.PROFILE_PHP + "?userid=" + mUserId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          JSONArray gamesArray = response.getJSONArray("games");
          // TODO: What if the user has no games?
          List<GameInfo> gameInfos = new ArrayList<GameInfo>(gamesArray.length());
          for (int i = 0; i < gamesArray.length(); i++) {
            JSONObject game = gamesArray.getJSONObject(i);
            GameInfo gameInfo = new GameInfo();
            gameInfo.id = game.getInt("game_id");
            gameInfo.placeId = game.getString("place_id");
            gameInfo.sportId = game.getInt("sport_id");
            gameInfo.result = game.getInt("result");
            gameInfos.add(gameInfo);
          }

          updateData(gameInfos);
          mGameInfos = gameInfos;

          Set<Sport> sports = new LinkedHashSet<Sport>();
          for (GameInfo gameInfo : gameInfos) {
            sports.add(Sports.getSport(gameInfo.sportId));
          }
          final List<Sport> sportList = new ArrayList<Sport>(sports);
          ((GridView) getView().findViewById(R.id.sport_list)).setAdapter(new ArrayAdapter<Sport>(getActivity(), 0, sportList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
              View newView = convertView;
              if (newView == null) {
                newView = inflater.inflate(R.layout.sport_icon, parent, false);
              }
              final Sport sport = sportList.get(position);
              ImageView iconView = (ImageView) newView.findViewById(android.R.id.icon);
              iconView.setImageResource(sport.iconResource());
              iconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if (v == mFilteredSportView) {
                    mFilteredSportView.setColorFilter(0);
                    mFilteredSportView = null;
                    mFilteredSport = null;
                    updateData(mGameInfos);
                    return;
                  }

                  if (mFilteredSportView != null) {
                    mFilteredSportView.setColorFilter(0);
                  }
                  updateData(gamesForSport(sport.id()));
                  ImageView imageView = (ImageView) v;
                  imageView.setColorFilter(v.getContext().getResources().getColor(R.color.woohoo_yes_color));
                  mFilteredSportView = imageView;
                  mFilteredSport = sport;
                }
              });
              return newView;
            }
          });
        } catch (JSONException e) {
          Log.e(logTag(ProfileFragment.this), "", e);
          getActivity().finish();
        }
      }
    });

    MyApplication.getInstance().addToRequestQueue(request);
  }

  private void updateData(List<GameInfo> gameInfos) {
    int totalWins = numResults(gameInfos, 1);
    int totalLosses = numResults(gameInfos, -1);;
    int totalTies = numResults(gameInfos, 0);;
    int winPct = (100 * totalWins) / (totalWins + totalLosses + totalTies);
    Pair<Integer, Integer> streak = streak(gameInfos);
    int winsLast10 = lastN(gameInfos, 10, 1);
    int lossesLast10 = lastN(gameInfos, 10, -1);
    int tiesLast10 = lastN(gameInfos, 10, 0);

    String streakString = String.format("%s-%d", RESULT_TO_LETTER.get(streak.first), streak.second);
    String last10String = String.format("%d-%d-%d", winsLast10, lossesLast10, tiesLast10);

    ((TextView) getView().findViewById(R.id.wins_text)).setText(String.valueOf(totalWins));
    ((TextView) getView().findViewById(R.id.losses_text)).setText(String.valueOf(totalLosses));
    ((TextView) getView().findViewById(R.id.ties_text)).setText(String.valueOf(totalTies));
    ((TextView) getView().findViewById(R.id.win_pct_text)).setText(String.valueOf(winPct));
    ((TextView) getView().findViewById(R.id.streak_text)).setText(streakString);
    ((TextView) getView().findViewById(R.id.last_10_text)).setText(last10String);

    List<Integer> gameIdList = getGameIds(gameInfos);
    mGameAdapter.setGameIds(gameIdList);

    ((TextView) getView().findViewById(R.id.game_count))
        .setText(String.format("Games: %d", gameIdList.size()));
    ((TextView) getView().findViewById(R.id.place_count))
        .setText(String.format("Places: %d", getPlaceCount(gameInfos)));
  }

  private List<GameInfo> gamesForSport(int sportId) {
    List<GameInfo> gameInfos = new LinkedList<>();
    for (GameInfo gameInfo : mGameInfos) {
      if (gameInfo.sportId == sportId) {
        gameInfos.add(gameInfo);
      }
    }
    return gameInfos;
  }

  private static List<Integer> getGameIds(List<GameInfo> gameInfos) {
    List<Integer> gameIds = new ArrayList<>(gameInfos.size());
    for (GameInfo gameInfo : gameInfos) {
      gameIds.add(gameInfo.id);
    }
    return gameIds;
  }

  private static int getPlaceCount(List<GameInfo> gameInfos) {
    Set<String> placeIds = new HashSet<>();
    for (GameInfo gameInfo : gameInfos) {
      placeIds.add(gameInfo.placeId);
    }
    return placeIds.size();
  }

  private static int lastN(List<GameInfo> games, int n, int result) {
    return numResults(games.subList(0, Math.min(n, games.size())), result);
  }

  private static Pair<Integer, Integer> streak(List<GameInfo> games) {
    int result = games.get(0).result;
    int count = 0;
    for (GameInfo game : games) {
      if (result == game.result) {
        count++;
      } else {
        break;
      }
    }
    return new Pair<>(result, count);
  }

  private static int numResults(List<GameInfo> games, int result) {
    int count = 0;
    for (GameInfo game : games) {
      count += (game.result == result) ? 1 : 0;
    }
    return count;
  }

  private static final class GameInfo {
    public int id;
    public int result;
    public int sportId;
    public String placeId;
  }

  private static final Map<Integer, String> RESULT_TO_LETTER = new HashMap<>();
  static {
    RESULT_TO_LETTER.put(-1, "L");
    RESULT_TO_LETTER.put(0, "T");
    RESULT_TO_LETTER.put(1, "W");
  }
}
