package io.bqbl;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.bqbl.utils.GameAdapter;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;


public class FeedFragment extends Fragment {
  private int mUserId;
  private GameAdapter mGameAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUserId = MyApplication.getCurrentUser(getActivity());
  }

  @Override
  public void onStart() {
    super.onStart();
    mGameAdapter = new GameAdapter(getActivity(), Collections.<Integer>emptyList());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  public void onResume() {
    super.onResume();

    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.feed_recycler);
    recyclerView.setHasFixedSize(false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(mGameAdapter);
    Request request = WebUtils.getRequest(URLs.FEED_PHP + "?userid=" + mUserId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          JSONArray gameIds = response.getJSONArray("game_ids");
          List<Integer> gameIdList = new ArrayList<>(gameIds.length());
          for (int i = 0; i < gameIds.length(); i++) {
            gameIdList.add(i, gameIds.getInt(i));
          }
          mGameAdapter.setGameIds(gameIdList);
        } catch (JSONException e) {
          Log.e(logTag(FeedFragment.this), "", e);
        }
      }
    });

    MyApplication.getInstance().addToRequestQueue(request);
  }
}
