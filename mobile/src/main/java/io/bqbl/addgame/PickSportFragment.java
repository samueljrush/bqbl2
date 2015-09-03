package io.bqbl.addgame;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.bqbl.R;
import io.bqbl.data.Sports;
import io.bqbl.data.Sports.Sport;

import static io.bqbl.MyApplication.logTag;

/**
 * Created by sam on 9/3/2015.
 */
public class PickSportFragment extends Fragment {
  private LayoutInflater mLayoutInflater;
  private AddGameActivity mActivity;

  private FragmentManager mFragmentManager;
  private PickSportFragment mPickSportFragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mActivity = (AddGameActivity) getActivity();
    mFragmentManager = getFragmentManager();
    mPickSportFragment = (PickSportFragment) getFragmentManager().findFragmentById(R.id.pick_sport_fragment);
    Log.d(logTag(this), "PSP");
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.addgame_pick_sport, container, false);
    ListAdapter adapter = new ArrayAdapter<Sport>(mActivity, R.layout.sport_row, Sports.getSports()) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View newView = (convertView != null) ? convertView : mLayoutInflater.inflate(R.layout.sport_row, parent, false);
        Sport sport = getItem(position);
        TextView sportNameView = (TextView) newView.findViewById(R.id.name);
        ImageView sportIconView = (ImageView) newView.findViewById(R.id.icon);
        sportNameView.setText(sport.name());
        sportIconView.setImageResource(sport.iconResource());
        return newView;
      }
    };

    Log.d(logTag(this), "Num sports: " + adapter.getCount());
    GridView gridView = (GridView) v.findViewById(R.id.sport_grid);
    gridView.setAdapter(adapter);
    return v;
  }
}
