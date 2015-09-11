package io.bqbl.addgame;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import io.bqbl.MyApplication;
import io.bqbl.R;
import io.bqbl.comments.CommentAdapter;
import io.bqbl.data.Game;
import io.bqbl.data.Place;
import io.bqbl.data.Sports;
import io.bqbl.data.Team;
import io.bqbl.data.User;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

public class PickScoreFragment extends Fragment {
  private RecyclerView mRecyclerView;
  private AddGameActivity mActivity;
  private LayoutInflater mLayoutInflater;
  private ListAdapter mAdapter;
  private List<Team> teams;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = (AddGameActivity) getActivity();
    mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.addgame_pick_score, container, false);
    view.findViewById(R.id.button_next).setOnClickListener(mSubmitClickListener);
    final GridView gridView = (GridView) view.findViewById(R.id.user_grid);

    final Game game = mActivity.mGame;
    ((AppCompatActivity) getActivity()).getSupportActionBar()
        .setTitle("Enter Scores");
    ArrayAdapter<Team> adapter = new ArrayAdapter<Team>(mActivity, R.layout.team_card, game.teams()) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(logTag(this), "Getting team view: " + position);
        final View newView = mLayoutInflater.inflate(R.layout.team_card_editable, parent, false);
        ListView teamUserList = (ListView) newView.findViewById(R.id.user_list);
        final Team team = game.teams().get(position);
        teamUserList.setAdapter(new UserListAdapter(team.users(), game));
        //TextView rank = (TextView) newView.findViewById(R.id.rank);
        //rank.setText(getTeamResultShortText(team));
        EditText score = (EditText) newView.findViewById(R.id.score);
        if (game.sportId() == Sports.GAMBLING_ID_SPECIAL_CASE) {
          score.setHint(R.string.enter_gambling_winnings);
        }
        score.setInputType(InputType.TYPE_CLASS_NUMBER);
        score.setKeyListener(DigitsKeyListener.getInstance("-0123456789."));
        score.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            team.score = s.toString();
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
        });
        View header = newView.findViewById(R.id.team_header);
        header.setBackgroundColor(mActivity.getResources().getColor(R.color.badge_tie));
        return newView;
      }
    };

    mAdapter = adapter;

    final TextView dateView = (TextView) view.findViewById(R.id.date_text);
    dateView.setText(CommentAdapter.getDateString(mActivity.mGame.date()));
    final TextView placeNameView = (TextView) view.findViewById(R.id.place_name);
    Place.requestPlace(mActivity.mGame.placeId, true, new Listener<Place>() {
      @Override
      public void onResult(Place place) {
        placeNameView.setText(place.name());
      }
    });
    gridView.setAdapter(adapter);
    //Log.d(logTag(this), "Adapter size " + adapter.getCount());
    return view;
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

  public void submit() {
    TreeMap<Double, List<Team>> scoreToTeamMap = new TreeMap<>();
    HashMap<Team, Double> teamToScoreValuesMap = new HashMap<>();

    List<Team> teams = mActivity.mGame.teams;
    for (int i = 0; i < teams.size(); i++) {
      Team team = teams.get(i);
      Double score = Double.valueOf(team.score());
      if (scoreToTeamMap.containsKey(score)) {
        scoreToTeamMap.get(score).add(team);
      } else {
        List<Team> teamList = new ArrayList<Team>();
        teamList.add(team);
        scoreToTeamMap.put(score, teamList);
      }
    }

    Collection<Double> scoresInOrder = null;
    int rank = 1;
    double prevScore = Double.MIN_VALUE;
    int type = Sports.getSport(mActivity.mGame.sportId).scoreType();
    switch (type) {
      case 3:
      case 7:
        scoresInOrder = scoreToTeamMap.keySet();
        break;
      default:
        scoresInOrder = scoreToTeamMap.descendingKeySet();
    }
    for (Double score : scoresInOrder) {
      List<Team> teamsAtScore = scoreToTeamMap.get(score);
      boolean tie = teamsAtScore.size() > 1;
      for (Team team : teamsAtScore) {
        team.rank = rank;
        team.tie = tie;
      }
      rank += teamsAtScore.size();
    }

    Game.setResultTypes(teams);
    Log.d(logTag("DEBUGLOG"), "Final game: " + mActivity.mGame.toJSON().toString());

    final ProgressDialog progress = new ProgressDialog(mActivity);
    progress.setTitle("Adding game...");
    progress.show();
    MyApplication.getInstance().addToRequestQueue(WebUtils.postJsonRequest(URLs.ADD_GAME_PHP, mActivity.mGame.toJSON(), new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        progress.hide();
        mActivity.finish();
      }
    }), "Adding game");
  }

  private View.OnClickListener mSubmitClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      submit();
    }
  };
}
