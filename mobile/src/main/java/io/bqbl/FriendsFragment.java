package io.bqbl;

import android.app.Fragment;
import android.content.Intent;
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

import io.bqbl.utils.SharedPreferencesUtils;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

public class FriendsFragment extends Fragment {

  private static final String KEY_FRIEND_ID = "user_id";
  private static final String KEY_FIRST_NAME = "first_name";
  private static final String KEY_LAST_NAME = "last_name";
  private static final String KEY_WINS = "wins";
  private static final String KEY_LOSSES = "losses";
  private static final String KEY_TIES = "ties";
  private int mUserId;
  private MyApplication mApp;
  private final Adapter mAdapter = new Adapter(Collections.<JSONObject>emptyList());

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mApp = (MyApplication) getActivity().getApplicationContext();
    mUserId = SharedPreferencesUtils.getCurrentUser(getActivity());
  }

  public void onResume() {
    super.onResume();

    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.friends_recycler);
    recyclerView.setHasFixedSize(false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(mAdapter);
    Request request = WebUtils.getRequest(URLs.FRIENDS_PHP + "?userid=" + mUserId, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          JSONArray friendsArray = response.getJSONArray("friends");
          List<JSONObject> friendList = new ArrayList<>(friendsArray.length());
          for (int i = 0; i < friendsArray.length(); i++) {
            friendList.add(i, friendsArray.getJSONObject(i));
          }
          mAdapter.setFriends(friendList);
        } catch (JSONException e) {
          Log.e(MyApplication.logTag(FriendsFragment.this), "", e);
        }
      }
    });

    mApp.addToRequestQueue(request);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_friends, container, false);
  }

  private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<JSONObject> mFriends;

    public Adapter(List<JSONObject> friends) {
      mFriends = friends;
      setHasStableIds(true);
    }

    public void setFriends(List<JSONObject> friends) {
      mFriends = friends;
      notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
      return mFriends.size();
    }

    @Override
    public long getItemId(int position) {
      try {
        return mFriends.get(position).getInt(KEY_FRIEND_ID);
      } catch (JSONException e) {
        return -1;
      }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_card, parent, false);
      return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      holder.bind(mFriends.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      protected TextView mTextView;
      protected View mItemView;

      public ViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
        mTextView = (TextView) itemView.findViewById(R.id.friends_card_text);
      }

      public void bind(JSONObject friend) {
        Log.d(MyApplication.logTag(FriendsFragment.this), friend.toString());
        try {
          mTextView.setText(String.format("%s %s (%d-%d-%d)",
              friend.getString(KEY_FIRST_NAME),
              friend.getString(KEY_LAST_NAME),
              friend.getInt(KEY_WINS),
              friend.getInt(KEY_LOSSES),
              friend.getInt(KEY_TIES)));
        } catch (JSONException e) {
          Log.e(MyApplication.logTag(FriendsFragment.this), "", e);
        }
        mItemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ProfileFragment.class);
            startActivity(intent, null);
          }
        });
      }
    }
  }
}
