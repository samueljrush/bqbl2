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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.bqbl.MyApplication;
import io.bqbl.R;
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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mActivity = (AddGameActivity) getActivity();
    mFragmentManager = getFragmentManager();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.addgame_pick_team, container, false);
    final GridView gridView = (GridView) v.findViewById(R.id.sport_grid);
    MyApplication.getInstance().addToRequestQueue(WebUtils.getJsonRequest(URLs.USERS_PHP, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          JSONArray friendsArray = response.getJSONArray("friends");
          JSONArray otherArray = response.getJSONArray("other");
          List<UserInfo> userInfos = new ArrayList<UserInfo>(friendsArray.length() + otherArray.length());
          for (int i = 0; i < friendsArray.length(); i++) {
            JSONObject friend = friendsArray.getJSONObject(i);
            UserInfo friendInfo = new UserInfo();
            friendInfo.userId = friend.getInt("user_id");
            friendInfo.first = friend.getString("first_name");
            friendInfo.last = friend.getString("last_name");
            userInfos.add(friendInfo);
          }
          for (int i = 0; i < otherArray.length(); i++) {
            JSONObject other = otherArray.getJSONObject(i);
            UserInfo otherInfo = new UserInfo();
            otherInfo.userId = other.getInt("user_id");
            otherInfo.first = other.getString("first_name");
            otherInfo.last = other.getString("last_name");
            userInfos.add(otherInfo);
          }
          ListAdapter adapter = new ArrayAdapter<UserInfo>(mActivity, R.layout.sport_row, userInfos) {
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
                  Log.d(logTag(this), "View clicked at position " + position);
                  showDialogForUser(userInfo.userId);
                  //mActivity.switchFragments(mActivity.mPickDateFragment);
                }
              });
              return newView;
            }
          };


          gridView.setAdapter(adapter);
          Log.d(logTag(this), "Num users: " + adapter.getCount());
        } catch (Exception e) {

        }

      }
    }), "Requesting all users");

    v.findViewById(R.id.button_next).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mActivity.switchFragments(mActivity.mPickScoreFragment);
            Log.d(logTag("DEBUGLOG"), "Game:" + mActivity.mGame.toJSON().toString());
          }
        }
    );
    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    mActivity.getSupportActionBar().setTitle("Assign Teams");
    Log.d(logTag("DEBUGLOG"), "Game: " + mActivity.mGame.toString());
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
            Log.d(logTag("DEBUGLOG"), "Game: " + mActivity.mGame.toString());
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
