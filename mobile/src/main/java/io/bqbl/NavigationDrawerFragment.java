package io.bqbl;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import io.bqbl.data.User;
import io.bqbl.utils.Listener;
import io.bqbl.utils.URLs;

import static io.bqbl.MyApplication.logTag;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

  /**
   * Remember the position of the selected item.
   */
  private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

  /**
   * Per the design guidelines, you should show the drawer on launch until the user manually
   * expands it. This shared preference tracks this.
   */
  private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
  private static final int DEFAULT_NAV_ITEM = 1;  // Feed

  /**
   * A pointer to the current callbacks instance (the Activity).
   */
  private NavigationDrawerCallbacks mCallbacks;

  /**
   * Helper component that ties the action bar to the navigation drawer.
   */
  private ActionBarDrawerToggle mDrawerToggle;

  private DrawerLayout mDrawerLayout;
  private ListView mDrawerListView;
  private View mFragmentContainerView;

  private int mCurrentSelectedPosition = DEFAULT_NAV_ITEM;
  private boolean mFromSavedInstanceState;
  private boolean mUserLearnedDrawer;

  public NavigationDrawerFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Read in the flag indicating whether or not the user has demonstrated awareness of the
    // drawer. See PREF_USER_LEARNED_DRAWER for details.
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

    if (savedInstanceState != null) {
      mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
      mFromSavedInstanceState = true;
    }

    // Select either the default item (0) or the last selected item.
    selectItem(mCurrentSelectedPosition);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Indicate that this fragment would like to influence the set of actions in the action bar.
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    mDrawerListView = (ListView) root.findViewById(R.id.nav_list_view);
    mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
      }
    });

    mDrawerListView.setAdapter(new NavigationDrawerAdapter());
    mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
    return root;
  }

  public boolean isDrawerOpen() {
    return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
  }

  /**
   * Users of this fragment must call this method to set up the navigation drawer interactions.
   *
   * @param fragmentId   The android:id of this fragment in its activity's layout.
   * @param drawerLayout The DrawerLayout containing this fragment's UI.
   */
  public void setUp(int fragmentId, DrawerLayout drawerLayout) {
    mFragmentContainerView = getActivity().findViewById(fragmentId);
    mDrawerLayout = drawerLayout;

    // set a custom shadow that overlays the main content when the drawer opens
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);

    // ActionBarDrawerToggle ties together the the proper interactions
    // between the navigation drawer and the action bar app icon.
    mDrawerToggle = new ActionBarDrawerToggle(
        getActivity(),                    /* host Activity */
        mDrawerLayout,                    /* DrawerLayout object */
        R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
        R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
    ) {
      @Override
      public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if (!isAdded()) {
          return;
        }

        getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if (!isAdded()) {
          return;
        }

        if (!mUserLearnedDrawer) {
          // The user manually opened the drawer; store this flag to prevent auto-showing
          // the navigation drawer automatically in the future.
          mUserLearnedDrawer = true;
          SharedPreferences sp = PreferenceManager
              .getDefaultSharedPreferences(getActivity());
          sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
        }

        getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
      }
    };

    // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
    // per the navigation drawer design guidelines.
    if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
      mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    // Defer code dependent on restoration of previous instance state.
    mDrawerLayout.post(new Runnable() {
      @Override
      public void run() {
        mDrawerToggle.syncState();
      }
    });

    mDrawerLayout.setDrawerListener(mDrawerToggle);
  }

  private void selectItem(int position) {
    mCurrentSelectedPosition = position;
    if (mDrawerListView != null) {
      mDrawerListView.setItemChecked(position, true);
    }
    if (mDrawerLayout != null) {
      mDrawerLayout.closeDrawer(mFragmentContainerView);
    }
    if (mCallbacks != null) {
      mCallbacks.onNavigationDrawerItemSelected(position);
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mCallbacks = (NavigationDrawerCallbacks) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mCallbacks = null;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Forward the new configuration the drawer toggle component.
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // If the drawer is open, show the global app actions in the action bar. See also
    // showGlobalContextActionBar, which controls the top-left area of the action bar.
    if (mDrawerLayout != null && isDrawerOpen()) {
      inflater.inflate(R.menu.global, menu);
      showGlobalContextActionBar();
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Per the navigation drawer design guidelines, updates the action bar to show the global app
   * 'context', rather than just what's in the current screen.
   */
  private void showGlobalContextActionBar() {
    ActionBar actionBar = getActionBar();
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setTitle(R.string.app_name);
  }

  private ActionBar getActionBar() {
    return ((AppCompatActivity) getActivity()).getSupportActionBar();
  }

  private class NavigationDrawerAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    int[] stringIds = new int[] {R.string.nav_item_feed, R.string.nav_item_friends, R.string.nav_item_places};
    int[] iconIds = new int[] {R.drawable.ic_list_black_24dp, R.drawable.ic_people_black_24dp, R.drawable.ic_place_black_24dp};

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (position == 0) {
        Resources resources = getActivity().getResources();
        int statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));
        View view = mLayoutInflater.inflate(R.layout.drawer_user_item, parent, false);
        view.findViewById(R.id.nav_drawer_user).setPadding(0, statusBarHeight, 0, 0);
        ImageView userImageView = (ImageView) view.findViewById(R.id.user_photo);
        final TextView emailTextView = (TextView) view.findViewById(R.id.email_text);
        final TextView nameTextView = (TextView) view.findViewById(R.id.name_text);

        int userId = 1;//MyApplication.getCurrentUser(getActivity());
        String userPhotoUri = URLs.getUserPhotoUrl(userId);
        //WebUtils.setImageRemoteUri(userImageView, userPhotoUri);
        Log.d(logTag(this), "Requesting user...");
        /*MyApplication.getInstance().addToRequestQueue(User.requestUser(userId, new Listener<User>() {
          @Override
          public void onResult(User user) {
            Log.d(logTag(this), "User: " + user.toString());
            emailTextView.setText(user.email());
            nameTextView.setText(user.first() + " " + user.last());
          }
        }));*/
        return view;
      } else {
        int listPosition = position - 1;
        View itemRoot = convertView;
        if ((itemRoot == null) || (itemRoot.getId() != R.id.nav_list_item_root)) {
          itemRoot = mLayoutInflater.inflate(R.layout.drawer_list_item, parent, false);
        }

        TextView titleView = (TextView) itemRoot.findViewById(R.id.nav_list_item_title);
        ImageView itemView = (ImageView) itemRoot.findViewById(R.id.nav_list_item_icon);
        titleView.setText(stringIds[listPosition]);
        itemView.setImageResource(iconIds[listPosition]);
        return itemRoot;
      }
    }

    @Override
    public int getCount() {
      return 1 + stringIds.length;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public Object getItem(int position) {
      return null;
    }

    @Override
    public int getItemViewType(int position) {
      return (position == 0) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }
  }

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public static interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(int position);
  }
}
