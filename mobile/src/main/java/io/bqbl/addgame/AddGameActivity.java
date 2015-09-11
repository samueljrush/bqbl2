package io.bqbl.addgame;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import io.bqbl.MyApplication;
import io.bqbl.R;
import io.bqbl.data.Comment;
import io.bqbl.data.Game;
import io.bqbl.data.Team;

import static io.bqbl.MyApplication.logTag;


public class AddGameActivity extends AppCompatActivity {

  public static final String EXTRA_GAME_ID = "game_id";
  public static final int PLACE_PICKER_REQUEST = 1   ;
  public PickSportFragment mPickSportFragment = new PickSportFragment();
  public PickDateFragment mPickDateFragment = new PickDateFragment();
  public PickTimeFragment mPickTimeFragment = new PickTimeFragment();
  public PickPlaceFragment mPickPlaceFragment = new PickPlaceFragment();
  public PickTeamFragment mPickTeamFragment = new PickTeamFragment();
  public PickNumTeamsFragment mPickNumTeamsFragment = new PickNumTeamsFragment();
  public PickScoreFragment mPickScoreFragment = new PickScoreFragment();

  private int mMonth;
  private int mDayOfMonth;
  private int mYear;
  private int mHours;
  private int mMinutes;
  public Game mGame = new Game(-1, -1, MyApplication.getCurrentUser(), "", new Date(), new LinkedList<Team>(),
      new LinkedList<Integer>(), new LinkedList<Integer>(), new LinkedList<Comment>());
  public ChaiFragment mChaiFragment = new ChaiFragment();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_game);
    Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
    setSupportActionBar(toolbar);

    mPickDateFragment.setCancelable(false);
    mPickTimeFragment.setCancelable(false);

    getFragmentManager().beginTransaction()
        .replace(R.id.the_fragment, new PickSportFragment())
        .commit();
  }

  public void switchFragments(Fragment next) {
    if (next instanceof DialogFragment) {
      getFragmentManager().beginTransaction()
          .remove(getFragmentManager().findFragmentById(R.id.the_fragment));
      ((DialogFragment) next).show(getFragmentManager(), next.getClass().getSimpleName());
      return;
    }
    getFragmentManager()
        .beginTransaction()
        .replace(R.id.the_fragment, next)
        .show(next)
        //.addToBackStack(next.getClass().getSimpleName())
        .commit();
  }

  public void hideFragments() {
    getFragmentManager().beginTransaction()
        .hide(getFragmentManager().findFragmentById(R.id.the_fragment));
  }

  public void switchToPlacePicker() {
    try {
      Intent intent = new PlacePicker.IntentBuilder().build(this);
      // Start the intent by requesting a result,
      // identified by a request code.
      startActivityForResult(intent, AddGameActivity.PLACE_PICKER_REQUEST);
    } catch (Exception e) {
      Log.e(logTag(this), "Error getting PlacePicker intent", e);
    }
  }

  public void setSportId(int sportId) {
    mGame.sportId = sportId;
  }

  public void setDate(Date date) {
    mGame.date = date;
  }

  public void setPlaceId(String placeId) {
    mGame.placeId = placeId;
  }

  public void setTeams(List<Team> teams) {
    mGame.teams = teams;
  }

  public void setDate(int year, int month, int dayOfMonth) {
    mMonth = month;
    mYear = year;
    mDayOfMonth = dayOfMonth;
    setDate();
  }

  public void setTime(int hours, int minutes) {
    mHours = hours;
    minutes = minutes;
    setDate();
  }

  private void setDate() {
    Log.d(logTag(this), String.format("Date: %d/%d/%d %d:%d, Millis: %d", mMonth, mDayOfMonth, mYear, mHours, mMinutes, new GregorianCalendar(mYear, mMonth, mDayOfMonth, mHours, mMinutes).getTime().getTime()/1000));
    mGame.date = new GregorianCalendar(mYear, mMonth, mDayOfMonth, mHours, mMinutes).getTime();
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data) {
    //Log.d(logTag(this), String.format("request: %d, result: %d", requestCode, resultCode));
    if (requestCode == PLACE_PICKER_REQUEST
        && resultCode == Activity.RESULT_OK) {

      // The user has selected a place. Extract the name and address.
      final Place place = PlacePicker.getPlace(data, this);

      final String placeId = place.getId();
      setPlaceId(placeId);
      //String attributionHtml = PlacePicker.getAttributions(data);
      //CharSequence attributions = attributionHtml == null ? "" : Html.fromHtml(attributionHtml);
      //Log.d(logTag(this), "Switching to teams fragment");
      switchFragments(mPickNumTeamsFragment);
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }

  }

  public void setNumTeams(int numTeams) {
    mGame.teams = new ArrayList<>(numTeams);
    for (int i = 0; i < numTeams; i++) {
      Team team = new Team();
      team.teamId = i;
      mGame.teams.add(i, team);
    }
  }

  public int getNumTeams(){
    return mGame.teams().size();
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

    return super.onOptionsItemSelected(item);
  }

}
