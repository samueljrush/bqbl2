package io.bqbl.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import io.bqbl.GameActivity;
import io.bqbl.MyApplication;
import io.bqbl.ProfileActivity;
import io.bqbl.R;
import io.bqbl.comments.CommentActivity;
import io.bqbl.data.Game;
import io.bqbl.data.Place;
import io.bqbl.data.Sports;
import io.bqbl.data.Team;
import io.bqbl.data.User;

import static io.bqbl.MyApplication.logTag;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd");
  private final Activity mActivity;
  private List<Integer> mGameIds;

  public GameAdapter(Activity activity, List<Integer> gameIds) {
    mActivity = activity;
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
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    Game.requestGame(mGameIds.get(position), true, new Listener<Game>() {
      @Override
      public void onResult(Game game) {
        holder.bind(game);
      }
    });
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    protected View mItemView;
    protected TextView mDateTextView;
    protected TextView mTitleTextView;
    protected TextView mSubtitleTextView;
    protected ImageView mChipView;
    protected GridView mGridView;
    protected TextView mWoohoosAndCommentsTextView;
    protected Button mWoohooButton;
    protected Button mBoohooButton;
    protected Button mCommentButton;

    public ViewHolder(View itemView) {
      super(itemView);
      mItemView = itemView;
      //mDateTextView = (TextView) itemView.findViewById(R.id.date_textview);
      mTitleTextView = (TextView) itemView.findViewById(R.id.title_text);
      mSubtitleTextView = (TextView) itemView.findViewById(R.id.subtitle_text);
      mGridView = (GridView) itemView.findViewById(R.id.user_grid);
      mChipView = (ImageView) itemView.findViewById(R.id.chip_view);
      mChipView.setBackground(new ShapeDrawable(new OvalShape()));
      mWoohoosAndCommentsTextView = (TextView) itemView.findViewById(R.id.woohoos_and_comments);
      mWoohooButton = (Button) itemView.findViewById(android.R.id.button1);
      mBoohooButton = (Button) itemView.findViewById(android.R.id.button2);
      mCommentButton = (Button) itemView.findViewById(android.R.id.button3);
      mCommentButton.setText(itemView.getContext().getString(R.string.comment_button_text));
      mBoohooButton.setText(itemView.getContext().getString(R.string.boohoo_button_text));
      mWoohooButton.setText(itemView.getContext().getString(R.string.woohoo_button_text));
      mGridView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void bind(final Game game) {
      //mDateTextView.setText(DATE_FORMAT.format(game.date()));
      final Sports.Sport sport = Sports.getSport(game.sportId());
      Team team0 = game.teams().get(0);
      int user0 = team0.users().get(0);
      User.requestUser(user0, true, new Listener<User>() {
        @Override
        public void onResult(User user) {
          Log.d(logTag(this), String.format("Game %d showing user %s", game.id(), user));
          mTitleTextView.setText(String.format("%s %s crushed it!", user.first(), user.last()));
        }
      });

      Place.requestPlace(game.placeId(), true, new Listener<Place>() {
        @Override
        public void onResult(Place place) {
          mSubtitleTextView.setText(String.format("%s at %s",
              //sport.name(),
              DATE_FORMAT.format(game.date()),
              place.name()));
        }
      });

      ((ShapeDrawable) mChipView.getBackground()).getPaint().setColor(sport.color());
      mChipView.setImageResource(sport.iconResource());

      List<Pair<Integer, Team>> userResults = new LinkedList<>();
      List<Team> teams = game.teams();
      for (Team team : teams) {
        for (int userId : team.users()) {
          userResults.add(new Pair<>(userId, team));
        }
      }

      int numWoohoos = game.woohoos().size();
      int numBoohoos = game.boohoos().size();
      int numComments = game.comments().size();
      StringBuilder sb = new StringBuilder();
      if (numWoohoos > 0) {
        sb.append(numWoohoos);
        sb.append(" Woohoo");
        if (numWoohoos > 1) {
          sb.append("s");
        }
      }
      if (numBoohoos > 0) {
        if (sb.length() != 0) {
          sb.append("  ");
        }
        sb.append(numBoohoos);
        sb.append(" Boohoo");
        if (numBoohoos > 1) {
          sb.append("s");
        }
      }
      if (numComments > 0) {
        if (sb.length() != 0) {
          sb.append("  ");
        }
        sb.append(numComments);
        sb.append(" Comment");
        if (numComments > 1) {
          sb.append("s");
        }
      }
      if (sb.length() > 0) {
        mWoohoosAndCommentsTextView.setText(sb);
      } else {
        mWoohoosAndCommentsTextView.setVisibility(View.GONE);
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

      updateWoohooButton(game.woohoos().contains(MyApplication.getCurrentUser()));
      mWoohooButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          final int userId = MyApplication.getCurrentUser();
          final boolean hasWoohoo = game.woohoos().contains(userId);
          String url = URLs.getSetOohooUrl(userId, game.id(), hasWoohoo ? 0 : 1);
          Log.d(logTag("DEBUGLOG"), url);
          if (hasWoohoo) {
            game.woohoos().remove(Integer.valueOf(userId));
          } else {
            game.woohoos().add(userId);
            game.boohoos().remove(Integer.valueOf(userId));
            updateBoohooButton(false);
          }
          updateWoohooButton(!hasWoohoo);
          Request request = WebUtils.getJsonRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
          });
          MyApplication.getInstance().addToRequestQueue(request, "Sending Woohoo!");
        }
      });

      updateBoohooButton(game.boohoos().contains(MyApplication.getCurrentUser()));
      mBoohooButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          final int userId = MyApplication.getCurrentUser();
          final boolean hasBoohoo = game.boohoos().contains(MyApplication.getCurrentUser());
          String url = URLs.getSetOohooUrl(userId, game.id(), hasBoohoo ? 0 : -1);
          if (hasBoohoo) {
            game.boohoos().remove(Integer.valueOf(userId));
          } else {
            game.boohoos().add(userId);
            game.woohoos().remove(Integer.valueOf(userId));
            updateWoohooButton(false);
          }
          updateBoohooButton(!hasBoohoo);
          Request request = WebUtils.getJsonRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
          });
          MyApplication.getInstance().addToRequestQueue(request, "Sending Boohoo!");
        }
      });

      View.OnClickListener commentClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          CommentActivity.startActivity(mActivity, game);
        }
      };
      mCommentButton.setOnClickListener(commentClickListner);
      mWoohoosAndCommentsTextView.setOnClickListener(commentClickListner);

      mItemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          GameActivity.startActivity(mActivity, game);
        }
      });
    }

    private void updateWoohooButton(boolean hasWoohoo) {
      int woohooColor = hasWoohoo
          ? mItemView.getContext().getResources().getColor(R.color.woohoo_yes_color)
          : mCommentButton.getCurrentTextColor();
      mWoohooButton.setTextColor(woohooColor);
    }

    private void updateBoohooButton(boolean hasBoohoo) {
      int boohooColor = hasBoohoo
          ? mItemView.getContext().getResources().getColor(R.color.boohoo_yes_color)
          : mCommentButton.getCurrentTextColor();
      mBoohooButton.setTextColor(boohooColor);
    }
  }

  private class UserGridAdapter extends ArrayAdapter<Pair<Integer, Team>> {
    private LayoutInflater mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    public UserGridAdapter(List<Pair<Integer, Team>> objects) {
      super(mActivity, R.layout.user_grid_item, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      Pair<Integer, Team> userPair = getItem(position);
      final int userId = userPair.first;
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
          badgeColor = mActivity.getResources().getColor(R.color.badge_win);
          break;
        case LOSS:
          badgeText = "L";
          badgeColor = mActivity.getResources().getColor(R.color.badge_loss);
          break;
        case GOLD:
          badgeText = "1";
          badgeColor = mActivity.getResources().getColor(R.color.badge_gold);
          break;
        case SILVER:
          badgeText = "2";
          badgeColor = mActivity.getResources().getColor(R.color.badge_silver);
          break;
        case BRONZE:
          badgeText = "3";
          badgeColor = mActivity.getResources().getColor(R.color.badge_bronze);
          break;
        case RANK:
          badgeText = String.valueOf(team.rank());
          badgeColor = mActivity.getResources().getColor(R.color.badge_loss);
          break;
        case TIE:
          badgeText = "T";
        default: // drop-down
          badgeColor = mActivity.getResources().getColor(R.color.badge_tie);
          break;
      }
      TextView badgeTextView = (TextView) newView.findViewById(R.id.result_badge);
      badgeTextView.setText(badgeText);
      ((ShapeDrawable) badgeTextView.getBackground()).getPaint().setColor(badgeColor);

      newView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          User.requestUser(userId, true, new Listener<User>() {
            @Override
            public void onResult(User user) {
              ProfileActivity.startActivity(mActivity, user);
            }
          });
        }
      });
      return newView;
    }
  }
}
