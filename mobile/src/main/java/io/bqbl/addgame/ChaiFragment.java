package io.bqbl.addgame;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.bqbl.R;

/**
 * Created by sam on 9/5/2015.
 */
public class ChaiFragment extends Fragment {

  private AddGameActivity mActivity;
  private LayoutInflater mLayoutInflater;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_chai, container, false);
    return v;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mActivity = (AddGameActivity) getActivity();
  }

  @Override
  public void onStart() {
    super.onStart();
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mActivity.switchFragments(mActivity.mPickTeamFragment);
      }
    }, 3500);
  }
}
