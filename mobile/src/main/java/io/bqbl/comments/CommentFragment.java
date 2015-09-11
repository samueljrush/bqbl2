package io.bqbl.comments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;

import io.bqbl.MyApplication;
import io.bqbl.R;
import io.bqbl.data.Comment;
import io.bqbl.data.Game;
import io.bqbl.utils.ImageUtils;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;
import io.bqbl.utils.WebUtils;

import static io.bqbl.MyApplication.getCurrentUser;
import static io.bqbl.MyApplication.logTag;

/**
 * A placeholder fragment containing a simple view.
 */
public class CommentFragment extends Fragment {

  private int mGameId;
  private CommentAdapter mCommentAdapter = new CommentAdapter(Collections.<Comment>emptyList());
  private RecyclerView mRecyclerView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mGameId = getActivity().getIntent().getIntExtra(CommentActivity.EXTRA_GAME_ID, -1);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_comment, container, false);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.comment_recycler);

    Game.requestGame(mGameId, true, new Listener<Game>() {
      @Override
      public void onResult(final Game game) {
        final int userId = MyApplication.getCurrentUser();
        setUpNavView(userId, game, view);
        setUpCommentsView(userId, game, view);
      }
    });

    return view;
  }

  public void setUpCommentsView(final int userId, final Game game, final View view) {
    final EditText editText = (EditText) view.findViewById(R.id.new_comment);

    mCommentAdapter.setData(game.comments());


    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
          final Comment comment = Comment.create(-1, mGameId, getCurrentUser(), v.getText().toString(), new Date(System.currentTimeMillis()));
          //Log.d(logTag(this), "Comment json: " + comment.toJSON());
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
                      //Log.d(logTag(this), "Adding comment to game..." + comment.toString());
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

    public void setUpNavView(final int userId, final Game game, final View view) {
    final TextView textView = (TextView) view.findViewById(R.id.num_oohoos);
    textView.setText(String.format("%d people Oohoo'd this", game.boohoos().size() + game.woohoos().size()));
    final ImageView woohooButton = (ImageView) view.findViewById(R.id.woohoo_button);
    final ImageView boohooButton = (ImageView) view.findViewById(R.id.boohoo_button);
    updateOohooButton(woohooButton, game.woohoos().contains(userId));
    updateOohooButton(boohooButton, game.boohoos().contains(userId));

    woohooButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final boolean hasWoohoo = game.woohoos().contains(userId);
        String url = URLs.getSetOohooUrl(userId, game.id(), hasWoohoo ? 0 : 1);
        //Log.d(logTag("DEBUGLOG"), url);
        updateOohooButton(boohooButton, false);
        updateOohooButton(woohooButton, !hasWoohoo);
        if (hasWoohoo) {
          game.woohoos().remove(Integer.valueOf(userId));
        } else {
          game.woohoos().add(userId);
          game.boohoos().remove(Integer.valueOf(userId));
        }

        Request request = WebUtils.getJsonRequest(url, new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
          }
        });
        MyApplication.getInstance().addToRequestQueue(request, "Sending Woohoo!");
      }
    });

    boohooButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final boolean hasBoohoo = game.boohoos().contains(userId);
        String url = URLs.getSetOohooUrl(userId, game.id(), hasBoohoo ? 0 : 1);
        //Log.d(logTag("DEBUGLOG"), url);
        updateOohooButton(woohooButton, false);
        updateOohooButton(boohooButton, !hasBoohoo);
        if (hasBoohoo) {
          game.boohoos().remove(Integer.valueOf(userId));
        } else {
          game.boohoos().add(userId);
          game.woohoos().remove(Integer.valueOf(userId));
        }
        Request request = WebUtils.getJsonRequest(url, new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
          }
        });
        MyApplication.getInstance().addToRequestQueue(request, "Sending Boohoo!");
      }
    });

    view.findViewById(R.id.to_oohoos).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //Log.d(logTag("DEBUGLOG"), "onClick");
        ((CommentActivity) getActivity()).commentsToOohoos();
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private static void updateOohooButton(ImageView imageView, boolean positive) {
    if (positive) {
      imageView.clearColorFilter();
    } else {
      ImageUtils.setGrayScale(imageView);
    }
  }
}
