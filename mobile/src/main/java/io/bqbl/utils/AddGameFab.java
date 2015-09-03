package io.bqbl.utils;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import io.bqbl.addgame.AddGameActivity;
import io.bqbl.data.CacheManager;
import io.bqbl.data.Game;

/**
 * Created by sam on 9/3/2015.
 */
public class AddGameFab extends FloatingActionButton {
  private Context mContext;
  private Game mGame;

  public AddGameFab(Context context) {
    super(context);
    mContext = context;
    setOnClickListener(mListener);
  }

  public AddGameFab(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    setOnClickListener(mListener);
  }

  public AddGameFab(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    setOnClickListener(mListener);
  }

  public void setGame(Game game) {
    mGame = game;
  }

  View.OnClickListener mListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(mContext, AddGameActivity.class);
      if (mGame != null) {
        CacheManager.getInstance().addGame(mGame);
        intent.putExtra(AddGameActivity.EXTRA_GAME_ID, mGame.id());
      }

      mContext.startActivity(intent);
    }
  };
}
