package io.bqbl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import io.bqbl.utils.URLs;
import io.bqbl.utils.SharedPreferencesUtils;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.getTag;


public class FeedActivity extends Activity {

  private int mUserId;
  private MyApplication mApp;
  private final GameAdapter mGameAdapter = new GameAdapter(Collections.<Integer>emptyList());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mApp = (MyApplication) getApplicationContext();
    setContentView(R.layout.activity_feed);
    mUserId = SharedPreferencesUtils.getCurrentUser(this);
  }

  protected void onResume() {
    super.onResume();

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.feed_recycler);
    recyclerView.setHasFixedSize(false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
          Log.e(getTag(FeedActivity.this), "", e);
        }
      }
    });

    mApp.addToRequestQueue(request);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_feed, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
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
            Intent intent = new Intent(FeedActivity.this, GameActivity.class);
            intent.putExtra(GameActivity.EXTRA_GAME_ID, game.id());
            startActivity(intent, null);
          }
        });
      }
    }
  }
}
