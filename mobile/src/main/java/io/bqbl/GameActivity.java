package io.bqbl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import io.bqbl.data.Game;


public class GameActivity extends AppCompatActivity {

  public static final String EXTRA_GAME_ID = "game_id";

  private int mGameId;

  public static void startActivity(Context context, Game game) {
    startActivity(context, game.id());
  }

  public static void startActivity(Context context, int gameId) {
    Intent intent = new Intent(context, GameActivity.class);
    intent.putExtra(EXTRA_GAME_ID, gameId);
    context.startActivity(intent, null);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);
    mGameId = getIntent().getExtras().getInt(EXTRA_GAME_ID, -1);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_game, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
