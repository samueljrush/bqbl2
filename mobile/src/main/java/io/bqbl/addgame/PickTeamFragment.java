package io.bqbl.addgame;

/**
 * Created by sam on 9/3/2015.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.bqbl.MyApplication;
import io.bqbl.R;
import io.bqbl.data.Team;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.logTag;

/**
 * Created by sam on 9/3/2015.
 */
public class PickTeamFragment extends Fragment {
  private LayoutInflater mLayoutInflater;
  private AddGameActivity mActivity;
  private int mNumTeams;

  private FragmentManager mFragmentManager;

  private Button nextButton;
  private Team currentTeam;
  private Iterator<Team> teamIterator;
  private List<UserInfo> usersLeft;
  private ArrayAdapter<UserInfo> mAdapter;
  private GridView mGridView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mActivity = (AddGameActivity) getActivity();
    mFragmentManager = getFragmentManager();
    teamIterator = mActivity.mGame.teams().iterator();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.addgame_pick_team, container, false);
    mGridView = (GridView) v.findViewById(R.id.sport_grid);
    MyApplication.getInstance().addToRequestQueue(WebUtils.getJsonRequest(URLs.USERS_PHP, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          JSONArray friendsArray = response.getJSONArray("friends");
          JSONArray otherArray = response.getJSONArray("other");
          usersLeft = new ArrayList<UserInfo>(friendsArray.length() + otherArray.length());
          for (int i = 0; i < friendsArray.length(); i++) {
            JSONObject friend = friendsArray.getJSONObject(i);
            UserInfo friendInfo = new UserInfo();
            friendInfo.userId = friend.getInt("user_id");
            friendInfo.first = friend.getString("first_name");
            friendInfo.last = friend.getString("last_name");
            usersLeft.add(friendInfo);
          }
          for (int i = 0; i < otherArray.length(); i++) {
            JSONObject other = otherArray.getJSONObject(i);
            UserInfo otherInfo = new UserInfo();
            otherInfo.userId = other.getInt("user_id");
            otherInfo.first = other.getString("first_name");
            otherInfo.last = other.getString("last_name");
            usersLeft.add(otherInfo);
          }
          mAdapter = getAdapter(usersLeft);

          mGridView.setAdapter(mAdapter);
          //Log.d(logTag(this), "Num users: " + mAdapter.getCount());
        } catch (Exception e) {

        }

      }
    }), "Requesting all users");

    nextButton = (Button) v.findViewById(R.id.button_next);
    nextButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!teamIterator.hasNext()) {
              mActivity.switchFragments(mActivity.mPickScoreFragment);
              //Log.d(logTag("DEBUGLOG"), "Game:" + mActivity.mGame.toJSON().toString());
            } else {
              advanceToNextTeam();
            }
          }
        }
    );
    return v;
  }

  private ArrayAdapter<UserInfo> getAdapter(List<UserInfo> users) {
    //Log.d(logTag("DEBUGLOG"), "Num users in adapter: " + users.size());
    return new ArrayAdapter<UserInfo>(mActivity, R.layout.sport_row, users) {
      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {
        View newView = (convertView != null) ? convertView : mLayoutInflater.inflate(R.layout.sport_row, parent, false);
        final UserInfo userInfo = getItem(position);
        TextView sportNameView = (TextView) newView.findViewById(R.id.name);
        ImageView sportIconView = (ImageView) newView.findViewById(R.id.icon);
        sportNameView.setText(userInfo.first + " " + userInfo.last);
        WebUtils.setImageRemoteUri(sportIconView, URLs.getUserPhotoUrl(userInfo.userId));
        newView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            boolean isAdd = !currentTeam.users().contains(userInfo.userId);
            //Log.d(logTag(this), "View clicked at position " + position + ", isAdd: " + isAdd);
            if (isAdd) {
              currentTeam.users().add(userInfo.userId);
              v.setBackgroundColor(0xFF9FA8DA);
            } else {
              currentTeam.users().remove(Integer.valueOf(userInfo.userId));
              v.setBackgroundColor(0);  //invisible
            }
            updateNextButtonState();
            //mActivity.switchFragments(mActivity.mPickDateFragment);
          }
        });
        return newView;
      }
    };
  }

  private void updateNextButtonState() {
    if (currentTeam.users().isEmpty()) {
      nextButton.setEnabled(false);
      nextButton.setClickable(false);
    } else {
      nextButton.setEnabled(true);
      nextButton.setClickable(true);
    }
  }

  private void advanceToNextTeam() {
    for (int i = 0; i < usersLeft.size(); i++) {
      UserInfo userInfo = usersLeft.get(i);
      if (currentTeam.users().contains(Integer.valueOf(userInfo.userId))) {
        usersLeft.remove(userInfo);
      }
    }

    mAdapter = getAdapter(usersLeft);
    mGridView.setAdapter(mAdapter);
    currentTeam = teamIterator.next();
    setTitleForTeam(currentTeam);
    updateNextButtonState();
  }

  @Override
  public void onStart() {
    super.onStart();
    currentTeam = teamIterator.next();
    setTitleForTeam(currentTeam);
    updateNextButtonState();
    //Log.d(logTag("DEBUGLOG"), "Game: " + mActivity.mGame.toString());
  }

  public void setTitleForTeam(Team team) {
    mActivity.getSupportActionBar().setTitle(String.format("Who was on Team %d?", team.teamId() + 1));
  }

  public static class UserInfo {
    public int userId;
    public String first;
    public String last;
  }

  public void showDialogForUser(final int userId) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("Which team?");

// Set up the input
    final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input.setInputType(InputType.TYPE_CLASS_NUMBER);
    input.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
    builder.setView(input);

    builder.setPositiveButton("Ok",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            //Log.d(logTag("DEBUGLOG"), "Game: " + mActivity.mGame.toString());
            int teamId = Integer.valueOf(input.getText().toString()) - 1;
            try {
              mActivity.mGame.teams.get(teamId).users.add(userId);
            } catch (Exception e) {

            }
          }
        }
    ).create().show();
  }
}
