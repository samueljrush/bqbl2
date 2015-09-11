package io.bqbl;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.bqbl.data.Place;
import io.bqbl.data.Sports;
import io.bqbl.data.Sports.Sport;
import io.bqbl.utils.GameAdapter;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

public class PlaceFragment extends Fragment {

  private String mPlaceId;
  private GameAdapter mGameAdapter;
  private List<GameInfo> mGameInfos;
  private ImageView mFilteredSportView;
  private ImageView mHeader;
  private Sport mFilteredSport;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPlaceId = getActivity().getIntent().getStringExtra(PlaceActivity.EXTRA_PLACE_ID);
    mGameAdapter = new GameAdapter(getActivity(), Collections.<Integer>emptyList());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_place, container, false);
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    mHeader = (ImageView) view.findViewById(R.id.header);
    mHeader.setColorFilter(0x99FFFFFF);
    return view;
  }

  public void onResume() {
    super.onResume();
    final LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.place_recycler);
    recyclerView.setHasFixedSize(false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(mGameAdapter);
    Place.requestPlace(mPlaceId, true, new Listener<Place>() {
      @Override
      public void onResult(Place place) {
        WebUtils.setImageRemoteUri(mHeader, URLs.getPlacesPhotoUrl(place.id(), 1200));
        //((TextView) getView().findViewById(R.id.toolbar_title)).setText(place.name());
        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) getView().findViewById(R.id.collapsing_toolbar_layout);
        ctl.setTitle(place.name());
      }
    });


    //Log.d(logTag(this), "fetching: " + URLs.PLACE_HISTORY_PHP + "?id=" + mPlaceId);
    Request request = WebUtils.getJsonRequest(URLs.PLACE_HISTORY_PHP + "?id=" + mPlaceId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          //Log.d(logTag(this), "response: " + response.toString());
          JSONArray gamesArray = response.getJSONArray("games");
          // TODO: What if the user has no games?
          List<GameInfo> gameInfos = new ArrayList<GameInfo>(gamesArray.length());
          for (int i = 0; i < gamesArray.length(); i++) {
            JSONObject game = gamesArray.getJSONObject(i);
            GameInfo gameInfo = new GameInfo();
            gameInfo.id = game.getInt("game_id");
            gameInfo.sportId = game.getInt("sport_id");
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
          Log.e(logTag(PlaceFragment.this), "", e);
          getActivity().finish();
        }
      }
    });

    MyApplication.getInstance().addToRequestQueue(request);
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

  private void updateData(List<GameInfo> gameInfos) {
    List<Integer> gameIdList = getGameIds(gameInfos);
    mGameAdapter.setGameIds(gameIdList);
  }


  private static final class GameInfo {
    public int id;
    public int sportId;
  }

  private static final Map<Integer, String> RESULT_TO_LETTER = new HashMap<>();
  static {
    RESULT_TO_LETTER.put(-1, "L");
    RESULT_TO_LETTER.put(0, "T");
    RESULT_TO_LETTER.put(1, "W");
  }
}
