package io.bqbl;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import io.bqbl.data.Game;
import io.bqbl.utils.Listener;


public class GameActivity extends Activity {

  public static final String EXTRA_GAME_ID = "game_id";

  private int mGameId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);
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

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume() {
    mGameId = getIntent().getExtras().getInt(EXTRA_GAME_ID, -1);
    if (mGameId > -1) {
      Game.requestGame(mGameId, new Listener<Game>() {
        @Override
        public void onResult(Game game) {
          ((TextView) findViewById(R.id.game_textview)).setText(game.toString());
        }
      });
    }
  }
}
