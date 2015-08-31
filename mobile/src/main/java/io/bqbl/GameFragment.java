package io.bqbl;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.bqbl.data.Game;
import io.bqbl.utils.Listener;

public class GameFragment extends Fragment {

  private int mGameId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mGameId = getActivity().getIntent().getIntExtra(GameActivity.EXTRA_GAME_ID, -1);
    if (mGameId < 0) {
      getActivity().finish();
      return;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  public void onResume() {
    super.onResume();
    final TextView textView = (TextView) getView().findViewById(android.R.id.text1);
    Game.requestGame(mGameId, true, new Listener<Game>() {
      @Override
      public void onResult(Game game) {
        textView.setText(game.toString());
      }
    });
  }
}
