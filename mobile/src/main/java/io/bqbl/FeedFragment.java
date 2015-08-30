package io.bqbl;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import java.util.LinkedList;
import java.util.List;

import io.bqbl.data.Game;
import io.bqbl.data.Sports;
import io.bqbl.data.Sports.Sport;
import io.bqbl.data.Team;
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
      protected TextView mTitleTextView;
      protected TextView mSubtitleTextView;
      protected ImageView mChipView;
      protected GridView mGridView;
      protected View mItemView;

      public ViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        mTitleTextView = (TextView) itemView.findViewById(R.id.title_text);
        mSubtitleTextView = (TextView) itemView.findViewById(R.id.subtitle_text);
        mGridView = (GridView) itemView.findViewById(R.id.user_grid);
        mChipView = (ImageView) itemView.findViewById(R.id.chip_view);
        mChipView.setBackground(new ShapeDrawable(new OvalShape()));
      }

      public void bind(final Game game) {
        Sport sport = Sports.getSport(game.sportId());
        try {
          Team team0 = game.teams().get(0);
          Team team1 = game.teams().get(1);
          int user0 = team0.users().get(0);
          int user1 = team1.users().get(0);

          mTitleTextView.setText(String.format("%d beat %d at %s", user0, user1, sport.name()));
        } catch (IndexOutOfBoundsException e) {
          mTitleTextView.setText(game.toString());
        }

        ((ShapeDrawable) mChipView.getBackground()).getPaint().setColor(sport.color());
        mChipView.setImageResource(sport.iconResource());

        List<Pair<Integer, Team>> userResults = new LinkedList<>();
        List<Team> teams = game.teams();
        for (Team team : teams) {
          for (int userId : team.users()) {
            userResults.add(new Pair<>(userId, team));
          }
        }


        mGridView.setAdapter(new UserGridAdapter(userResults));

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

    private class UserGridAdapter extends ArrayAdapter<Pair<Integer, Team>> {
      private LayoutInflater mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      public UserGridAdapter(List<Pair<Integer, Team>> objects) {
        super(getActivity(), R.layout.user_grid_item, objects);
      }

      public View getView(int position, View convertView, ViewGroup parent) {
        Pair<Integer, Team> userPair = getItem(position);
        int userId = userPair.first;
        Team team = userPair.second;
        View newView = convertView;
        if (newView == null) {
          newView = mLayoutInflater.inflate(R.layout.user_grid_item, parent, false);
          newView.findViewById(R.id.result_badge).setBackground(new ShapeDrawable(new OvalShape()));
        }

        WebUtils.setBackgroundRemoteUri(newView, URLs.getUserPhotoUrl(userId));
        String badgeText = null;
        int badgeColor;
        switch (team.resultTypeHolder().value) {
          case WIN:
            badgeText = "W";
            badgeColor = getActivity().getResources().getColor(R.color.badge_win);
            break;
          case LOSS:
            badgeText = "L";
            badgeColor = getActivity().getResources().getColor(R.color.badge_loss);
            break;
          case GOLD:
            badgeText = "G";
            badgeColor = getActivity().getResources().getColor(R.color.badge_gold);
            break;
          case SILVER:
            badgeText = "S";
            badgeColor = getActivity().getResources().getColor(R.color.badge_silver);
            break;
          case BRONZE:
            badgeText = "B";
            badgeColor = getActivity().getResources().getColor(R.color.badge_bronze);
            break;
          case TIE:
            badgeText = "T";
          case RANK:
            badgeText = String.valueOf(team.rank());
          default: // drop-down
            badgeColor = getActivity().getResources().getColor(R.color.badge_tie);
            break;
        }
        TextView badgeTextView = (TextView) newView.findViewById(R.id.result_badge);
        badgeTextView.setText(badgeText);
        ((ShapeDrawable) badgeTextView.getBackground()).getPaint().setColor(badgeColor);

        return newView;
      }
    }
  }
}
