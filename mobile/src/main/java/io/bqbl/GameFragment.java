package io.bqbl;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.bqbl.comments.CommentActivity;
import io.bqbl.comments.CommentAdapter;
import io.bqbl.data.Comment;
import io.bqbl.data.Game;
import io.bqbl.data.Sports;
import io.bqbl.data.Team;
import io.bqbl.data.User;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.getCurrentUser;
import static io.bqbl.MyApplication.logTag;

public class GameFragment extends Fragment {

  private int mGameId;
  private CommentAdapter mCommentAdapter = new CommentAdapter(Collections.<Comment>emptyList());
  private RecyclerView mRecyclerView;
  private GameActivity mActivity;
  private LayoutInflater mLayoutInflater;
  protected TextView mWoohoosAndCommentsTextView;
  protected Button mWoohooButton;
  protected Button mBoohooButton;
  protected Button mCommentButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = (GameActivity) getActivity();
    mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mGameId = getActivity().getIntent().getIntExtra(GameActivity.EXTRA_GAME_ID, -1);
    if (mGameId < 0) {
      getActivity().finish();
      return;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_game, container, false);
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    final GridView gridView = (GridView) view.findViewById(R.id.user_grid);

    mWoohoosAndCommentsTextView = (TextView) view.findViewById(R.id.woohoos_and_comments);
    mWoohooButton = (Button) view.findViewById(android.R.id.button1);
    mBoohooButton = (Button) view.findViewById(android.R.id.button2);
    mCommentButton = (Button) view.findViewById(android.R.id.button3);
    mCommentButton.setText(view.getContext().getString(R.string.comment_button_text));
    mBoohooButton.setText(view.getContext().getString(R.string.boohoo_button_text));
    mWoohooButton.setText(view.getContext().getString(R.string.woohoo_button_text));

    Game.requestGame(mGameId, true, new Listener<Game>() {
      @Override
      public void onResult(final Game game) {
        ((AppCompatActivity) getActivity()).getSupportActionBar()
            .setTitle(Sports.getSport(game.sportId()).name());
        ListAdapter adapter = new ArrayAdapter<Team>(mActivity, R.layout.team_card, game.teams()) {
          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(logTag(this), "Getting team view: " + position);
            final View newView = mLayoutInflater.inflate(R.layout.team_card, parent, false);
            ListView teamUserList = (ListView) newView.findViewById(R.id.user_list);
            Team team = game.teams().get(position);
            teamUserList.setAdapter(new UserListAdapter(team.users(), game));
            TextView rank = (TextView) newView.findViewById(R.id.rank);
            rank.setText(getTeamResultShortText(team));
            TextView score = (TextView) newView.findViewById(R.id.score);
            score.setText(team.score());
            View header = newView.findViewById(R.id.team_header);
            header.setBackgroundColor(getTeamResultColor(team, getActivity().getResources()));
            return newView;
          }
        };

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
        gridView.setAdapter(adapter);
        Log.d(logTag(this), "Adapter size " + adapter.getCount());
      }
    });
    return view;
  }

  private void updateWoohooButton(boolean hasWoohoo) {
    int woohooColor = hasWoohoo
        ? mActivity.getResources().getColor(R.color.woohoo_yes_color)
        : mCommentButton.getCurrentTextColor();
    mWoohooButton.setTextColor(woohooColor);
  }

  private void updateBoohooButton(boolean hasBoohoo) {
    int boohooColor = hasBoohoo
        ? mActivity.getResources().getColor(R.color.boohoo_yes_color)
        : mCommentButton.getCurrentTextColor();
    mBoohooButton.setTextColor(boohooColor);
  }
  public void setUpCommentsView(final int userId, final Game game, final View view) {
    final EditText editText = (EditText) view.findViewById(R.id.new_comment);

    mCommentAdapter.setData(game.comments());


    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
          final Comment comment = Comment.create(-1, mGameId, getCurrentUser(), v.getText().toString(), new Date(System.currentTimeMillis()));
          Log.d(logTag(this), "Comment json: " + comment.toJSON());
          try {
            final JSONObject requestJson = new JSONObject();
            MyApplication.getInstance().addToRequestQueue(
                WebUtils.postJsonRequest(URLs.ADD_COMMENT_PHP, comment.toJSON(), new Response.Listener<JSONObject>() {
                  @Override
                  public void onResponse(JSONObject response) {
                    try {
                      Date date = new Date(response.getLong(Comment.JSON_KEY_DATE) * 1000);
                      int id = response.getInt(Comment.JSON_KEY_COMMENT_ID);
                      game.comments().add(Comment.create(id, comment.gameId(), comment.userId(), comment.text(), date));
                      int numComments = game.comments().size();
                      Log.d(logTag(this), "Adding comment to game..." + comment.toString());
                      mCommentAdapter.notifyItemInserted(numComments - 1);
                      mRecyclerView.scrollToPosition(numComments - 1);
                      editText.setText(null);
                      InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                      imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    } catch (Exception e) {
                      Log.e(logTag(this), "JSONException adding comment", e);
                    }
                  }
                }), "Sending comment.");
          } catch (Exception e) {

          }
          return true;
        }
        return false;
      }
    });

    mRecyclerView.setHasFixedSize(false);
    //     LinearLayoutManager linearLayoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(mCommentAdapter);
    int i = 0;
    final int commentId = getActivity().getIntent().getIntExtra(CommentActivity.EXTRA_COMMENT_ID, -1);
    for (Comment comment : game.comments()) {
      if (comment.id() == commentId) {
        mRecyclerView.scrollToPosition(i);
        break;
      }
      i++;
    }
  }

  private class UserListAdapter extends ArrayAdapter<Integer> {

    private Game mGame;

    public UserListAdapter(List<Integer> objects, Game game) {
      super(mActivity, R.layout.user_list_item, objects);
      mGame = game;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      final int userId = getItem(position);

      final View newView = mLayoutInflater.inflate(R.layout.user_list_item, parent, false);

      ImageView photoView = (ImageView) newView.findViewById(R.id.user_photo);
      WebUtils.setImageRemoteUri(photoView, URLs.getUserPhotoUrl(userId));

      User.requestUser(userId, true, new Listener<User>() {
        @Override
        public void onResult(User user) {
          TextView textView = ((TextView) newView.findViewById(R.id.name));
          if (user.first().length() > 9) {
            textView.setText(String.format("%s. %s", user.first().substring(0, 1), user.last()));
          } else if (user.last().length() > 9) {
            textView.setText(String.format("%s %s.", user.first(), user.last().substring(0, 1)));
          } else {
            textView.setText(user.name());
          }
        }
      });
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

  public static int getTeamResultColor(Team team, Resources res) {
    switch (team.resultTypeHolder().value) {
      case WIN:
        return res.getColor(R.color.badge_win);
      case LOSS:
        return res.getColor(R.color.badge_loss);
      case GOLD:
        return res.getColor(R.color.badge_gold);
      case SILVER:
        return res.getColor(R.color.badge_silver);
      case BRONZE:
        return res.getColor(R.color.badge_bronze);
      case RANK:
        return res.getColor(R.color.badge_loss);
      case TIE:
      default: // drop-down
        return res.getColor(R.color.badge_tie);
    }
  }

  public static CharSequence getTeamResultShortText(Team team) {
    switch (team.resultTypeHolder().value) {
      case WIN:
        return "W";
      case LOSS:
        return "L";
      case GOLD:
      case SILVER:
      case BRONZE:
      case RANK:
        return ordinal(team.rank());
      case TIE:
        return "T-" + ordinal(team.rank());
      default: // drop-down
        return "";
    }
  }

  public static CharSequence ordinal(int i) {
    CharSequence th = Html.fromHtml("<sup>th</sup>");
    CharSequence[] sufixes = new CharSequence[]{
        th,
        Html.fromHtml("<sup>st</sup>"),
        Html.fromHtml("<sup>nd</sup>"),
        Html.fromHtml("<sup>rd</sup>"),
        th, th, th, th, th, th};
    SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(i));
    switch (i % 100) {
      case 11:
      case 12:
      case 13:
        return ssb.append(sufixes[5]);
      default:
        return ssb.append(sufixes[i % 10]);

    }
  }
}
