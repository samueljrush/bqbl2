package io.bqbl.comments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.bqbl.R;
import io.bqbl.data.Game;

import static io.bqbl.MyApplication.logTag;

public class CommentActivity extends Activity {
  public static final String EXTRA_GAME_ID = "game_id";
  public static final String EXTRA_COMMENT_ID = "comment_id";
  private static final String KEY_OOHOO_STATE = "oohoo_state";
  private static final String KEY_IN_OOHOO_FRAGMENT = "in_oohoo_fragment";

  private OohooFragment mOohooFragment;
  private CommentFragment mCommentFragment;

  private boolean mIsInOohoosFragment = false;

  public static void startActivity(Context context, Game game) {
    Intent intent = new Intent(context, CommentActivity.class);
    intent.putExtra(EXTRA_GAME_ID, game.id());
    context.startActivity(intent, null);
  }

  public static void startActivity(Context context, int gameId, int commentId) {
    Intent intent = new Intent(context, CommentActivity.class);
    intent.putExtra(EXTRA_GAME_ID, gameId);
    intent.putExtra(EXTRA_COMMENT_ID, commentId);
    context.startActivity(intent, null);
  }

  public void commentsToOohoos() {
    Log.d(logTag(this), "switching To Oohoos");
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
        .hide(mCommentFragment)
        .show(mOohooFragment)
        .commit();
    mIsInOohoosFragment = true;
  }

  public void oohoosToComments() {
    Log.d(logTag(this), "switching To Comments");
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
        .hide(mOohooFragment)
        .show(mCommentFragment)
        .commit();
    mIsInOohoosFragment = false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);
    mCommentFragment = (CommentFragment) getFragmentManager().findFragmentById(R.id.comments_fragment);
    mOohooFragment = (OohooFragment) getFragmentManager().findFragmentById(R.id.oohoos_fragment);

    getFragmentManager().beginTransaction().hide(mOohooFragment).commit();
    Log.d(logTag(this), "commentFrag: " + mCommentFragment + " oohooFrag: " + mOohooFragment);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_comments, menu);
    return true;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(KEY_OOHOO_STATE, mOohooFragment.getOohooState());
    outState.putBoolean(KEY_IN_OOHOO_FRAGMENT, mIsInOohoosFragment);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState == null) {
      return;
    }
    if (savedInstanceState.containsKey(KEY_OOHOO_STATE)) {
      mOohooFragment.setOohooState(savedInstanceState.getBoolean(KEY_OOHOO_STATE));
    }
    if (savedInstanceState.getBoolean(KEY_IN_OOHOO_FRAGMENT, false)) {
      commentsToOohoos();
    }

    super.onRestoreInstanceState(savedInstanceState);
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
}
