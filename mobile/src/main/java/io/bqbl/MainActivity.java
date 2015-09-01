package io.bqbl;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static io.bqbl.MyApplication.logTag;


public class MainActivity extends AppCompatActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private NavigationDrawerFragment mNavigationDrawerFragment;

  private Fragment mFeedFragment;
  private Fragment mFriendsFragment;
  private Fragment mPlacesFragment;

  /**
   * Used to store the last screen title. For use in {@link #restoreActionBar()}.
   */
  private CharSequence mTitle;
  private DrawerLayout mDrawerLayout;
  private Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
    mTitle = getTitle();

    mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
    android.util.Log.d(logTag("DEBUGLOG"), "toolbar null: " + (mToolbar == null));
    setSupportActionBar(mToolbar);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    // Set up the drawer.
    mNavigationDrawerFragment.setUp(
        R.id.navigation_drawer,
        mDrawerLayout);
    Log.d(logTag(this), "Main Activity Created");
  }

  @Override
  public void onNavigationDrawerItemSelected(int position) {
    // update the main content by replacing fragments
    FragmentManager fragmentManager = getFragmentManager();
    Fragment newFragment;
    switch (position) {
      case 0:
        // TODO(sam): user thing was clicked, should do something.
        newFragment = null;
        break;
      case 2:
        if (mFriendsFragment == null) {
          mFriendsFragment = new FriendsFragment();
        }
        newFragment = mFriendsFragment;
        break;
      case 3:
        if (mPlacesFragment == null) {
          mPlacesFragment = new FeedFragment();
        }
        newFragment = mPlacesFragment;
        break;
      case 1:
      default:
        if (mFeedFragment == null) {
          mFeedFragment = new FeedFragment();
        }
        newFragment = mFeedFragment;
        break;
    }

    if (newFragment != null) {
      fragmentManager.beginTransaction()
          .replace(R.id.container, newFragment)
          .addToBackStack(null)
          .commit();
    }
  }

  public void onSectionAttached(int number) {
    switch (number) {
      case 1:
        mTitle = getString(R.string.nav_item_feed);
        break;
      case 2:
        mTitle = getString(R.string.nav_item_friends);
        break;
      case 3:
        mTitle = getString(R.string.nav_item_places);
        break;
    }
  }

  public void restoreActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(true);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (!mNavigationDrawerFragment.isDrawerOpen()) {
      // Only show items in the action bar relevant to this screen
      // if the drawer is not showing. Otherwise, let the drawer
      // decide what to show in the action bar.
      getMenuInflater().inflate(R.menu.main, menu);
      restoreActionBar();
      return true;
    }
    return super.onCreateOptionsMenu(menu);
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
