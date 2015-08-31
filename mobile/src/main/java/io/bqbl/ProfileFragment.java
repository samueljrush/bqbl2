package io.bqbl;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.List;

import io.bqbl.data.Sports;
import io.bqbl.data.Sports.Sport;
import io.bqbl.data.User;
import io.bqbl.utils.GameAdapter;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

public class ProfileFragment extends Fragment {

  private int mUserId;
  private GameAdapter mGameAdapter;


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
          JSONArray sportTotalsArray = response.getJSONArray("sports");
          List<JSONObject> sportTotals = new ArrayList<JSONObject>(sportTotalsArray.length());
          int totalWins = 0;
          int totalLosses = 0;
          int totalTies = 0;
          for (int i = 0; i < sportTotalsArray.length(); i++) {
            JSONObject sport = sportTotalsArray.getJSONObject(i);
            sportTotals.add(sport);
            int wins = sport.getInt("wins");
            int losses = sport.getInt("losses");
            int ties = sport.getInt("ties") ;
            totalWins += wins;
            totalLosses += losses;
            totalTies += ties;
          }
          int winPct = (100 * totalWins) / (totalWins + totalLosses + totalTies);
          // TODO(Streak and Last 10)
          ((TextView) getView().findViewById(R.id.wins_text)).setText(String.valueOf(totalWins));
          ((TextView) getView().findViewById(R.id.losses_text)).setText(String.valueOf(totalLosses));
          ((TextView) getView().findViewById(R.id.ties_text)).setText(String.valueOf(totalTies));
          ((TextView) getView().findViewById(R.id.win_pct_text)).setText(String.valueOf(winPct));

          final List<Sport> sports = new ArrayList<Sport>(sportTotals.size());
          for (JSONObject sportJson : sportTotals) {
            sports.add(Sports.getSport(sportJson.getInt("sport_id")));

          }
          ((GridView) getView().findViewById(R.id.sport_list)).setAdapter(new ArrayAdapter<Sport>(getActivity(), 0, sports) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
              View newView = convertView;
              if (newView == null) {
                newView = inflater.inflate(R.layout.sport_icon, parent, false);
              }
              Sport sport = sports.get(position);
              ((ImageView) newView.findViewById(android.R.id.icon)).setImageResource(sport.iconResource());
              return newView;
            }
          });

          JSONArray gameIds = response.getJSONArray("game_ids");
          List<Integer> gameIdList = new ArrayList<>(gameIds.length());
          for (int i = 0; i < gameIds.length(); i++) {
            gameIdList.add(i, gameIds.getInt(i));
          }
          mGameAdapter.setGameIds(gameIdList);

          ((TextView) getView().findViewById(R.id.game_count))
              .setText(String.format("Games: %d", gameIdList.size()));
          ((TextView) getView().findViewById(R.id.place_count))
              .setText(String.format("Places: %d", 0 /* TODO: add to JSON */));
        } catch (JSONException e) {
          Log.e(MyApplication.logTag(ProfileFragment.this), "", e);
        }
      }
    });

    MyApplication.getInstance().addToRequestQueue(request);
  }
}
