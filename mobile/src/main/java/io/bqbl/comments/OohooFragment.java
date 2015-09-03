package io.bqbl.comments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import io.bqbl.MyApplication;
import io.bqbl.R;
import io.bqbl.data.CacheManager;
import io.bqbl.data.Game;
import io.bqbl.utils.Listener;
import io.bqbl.utils.OnSwipeTouchListener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.getCurrentUser;
import static io.bqbl.MyApplication.logTag;

/**
 * A placeholder fragment containing a simple view.
 */
public class OohooFragment extends Fragment {

  private int mGameId;
  private OohooAdapter mWoohooAdapter;
  private OohooAdapter mBoohooAdapter;
  private OohooAdapter mCurrentAdapter;
  private RecyclerView mRecyclerView;
  private TextView mWoohoosText;
  private TextView mBoohoosText;
  private View mWoohoosTab;
  private View mBoohoosTab;
  private Collection<Integer> mFriends;
  private Collection<Listener<Collection<Integer>>> mFriendsListeners = new LinkedList<>();
  private Boolean mCurrentOohooType = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mGameId = getActivity().getIntent().getIntExtra(CommentActivity.EXTRA_GAME_ID, -1);
    mWoohooAdapter = new OohooAdapter(this, Collections.<Integer>emptyList());
    mBoohooAdapter = new OohooAdapter(this, Collections.<Integer>emptyList());
    Game.requestGame(mGameId, true, new Listener<Game>() {
      @Override
      public void onResult(Game game) {
        mWoohooAdapter.setData(game.woohoos());
        mBoohooAdapter.setData(game.boohoos());
      }
    });
    if (CacheManager.getInstance().getCurrentUserFriends() != null) {
      mFriends = CacheManager.getInstance().getCurrentUserFriends();
    } else {
      MyApplication.getInstance().addToRequestQueue(
          WebUtils.getJsonRequest(URLs.FRIENDS_PHP + "?userid=" + getCurrentUser(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              Collection<Integer> friendsCollection = new HashSet<Integer>();
              try {
                JSONArray friends = response.getJSONArray("friends");
                for (int i = 0; i < friends.length(); i++) {
                  friendsCollection.add(friends.getJSONObject(i).getInt("user_id"));
                }
              } catch (Exception e) {
                Log.e(logTag(OohooFragment.this), "Error parsing json", e);
              }
              mFriends = friendsCollection;
              Log.d(logTag(this), String.format("found %d friends, notifying %d listeners", mFriends.size(), mFriendsListeners.size()));
              for (Listener<Collection<Integer>> listener : mFriendsListeners) {
                listener.onResult(mFriends);
              }
              CacheManager.getInstance().setCurrentUserFriends((HashSet<Integer>) mFriends);
            }
          }), "Requesting friends");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_oohoo, container, false);
    final TextView textView = (TextView) view.findViewById(R.id.num_oohoos);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.comment_recycler);
    mWoohoosText = (TextView) view.findViewById(R.id.woohoo_title);
    mBoohoosText = (TextView) view.findViewById(R.id.boohoo_title);
    mWoohoosTab = view.findViewById(R.id.woohoo_tab);
    mBoohoosTab = view.findViewById(R.id.boohoo_tab);
    view.findViewById(R.id.to_comments).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((CommentActivity) getActivity()).oohoosToComments();
      }
    });

    mWoohoosText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mRecyclerView.setAdapter(mWoohooAdapter);
        setOohooType(true);
      }
    });
    mBoohoosText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mRecyclerView.setAdapter(mBoohooAdapter);
        setOohooType(false);
      }
    });

    view.findViewById(R.id.oohoo_switcher).setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
      @Override
      public void onSwipeRight() {
        setOohooType(false);  // to Boohoos
      }

      @Override
      public void onSwipeLeft() {
        setOohooType(true);  // to Woohoos
      }
    });

    mRecyclerView.setHasFixedSize(false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(linearLayoutManager);

    if (mCurrentOohooType == null) {
      Game.requestGame(mGameId, true, new Listener<Game>() {
        @Override
        public void onResult(final Game game) {
          setOohooType(!game.woohoos().isEmpty() || game.boohoos().isEmpty());
        }
      });
    } else {
      setOohooType(mCurrentOohooType);
    }
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  public void requestFriends(Listener<Collection<Integer>> listener) {
    if (mFriends != null) {
      listener.onResult(mFriends);
    }
    mFriendsListeners.add(listener);
  }

  public void setOohooType(boolean positive) {
    mCurrentOohooType = positive;
    if (mRecyclerView == null) {
      return;
    }

    OohooAdapter newAdapter = positive ? mWoohooAdapter : mBoohooAdapter;
    if (mRecyclerView.getAdapter() != newAdapter) {
      mRecyclerView.setAdapter(newAdapter);
    }

    final View nonSelectedView = positive ? mWoohoosTab : mBoohoosTab;
    final View selectedView = positive ? mBoohoosTab : mWoohoosTab;

    selectedView.setBackgroundColor(0xFFFFFFFF);
    nonSelectedView.setBackgroundColor(0xFFAAAAAA);
    Log.d(logTag(this), String.format("Setting %s to dark", nonSelectedView));
  }

  public boolean getOohooState() {
    return mCurrentOohooType != null ? mCurrentOohooType : true;
  }

  public void setOohooState(Boolean oohooState) {
    mCurrentOohooType = oohooState;
  }
}
