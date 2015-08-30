package io.bqbl;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.bqbl.data.Game;
import io.bqbl.utils.Listener;
import io.bqbl.utils.SharedPreferencesUtils;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;


public class FeedFragment extends Fragment {

  private int mUserId;
  private MyApplication mApp;
  private final GameAdapter mGameAdapter = new GameAdapter(Collections.<Integer>emptyList());

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mApp = (MyApplication) getActivity().getApplicationContext();
    mUserId = SharedPreferencesUtils.getCurrentUser(getActivity());
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

    mApp.addToRequestQueue(request);
  }

  private class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private List<Integer> mGameIds;

    public GameAdapter(List<Integer> gameIds) {
      mGameIds = gameIds;
      setHasStableIds(true);
    }

    public void setGameIds(List<Integer> gameIds) {
      mGameIds = gameIds;
      notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
      return mGameIds.size();
    }

    @Override
    public long getItemId(int position) {
      return mGameIds.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card, parent, false);
      return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      mApp.addToRequestQueue(
          Game.requestGame(mGameIds.get(position), new Listener<Game>() {
            @Override
            public void onResult(Game game) {
              holder.bind(game);
            }
          }));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      protected TextView mTextView;
      protected View mItemView;

      public ViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        mTextView = (TextView) itemView.findViewById(R.id.feed_card_text);
      }

      public void bind(final Game game) {
        mTextView.setText(game.toString());
        mItemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //Intent intent = new Intent(FeedFragment.this, GameActivity.class);
            //intent.putExtra(GameActivity.EXTRA_GAME_ID, game.id());
            //startActivity(intent, null);
          }
        });
      }
    }
  }
}
