package io.bqbl.addgame;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.bqbl.R;

/**
 * Created by sam on 9/3/2015.
 */
public class PickDateFragment extends DialogFragment {
  private LayoutInflater mLayoutInflater;
  private AddGameActivity mActivity;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mActivity = (AddGameActivity) getActivity();
  }

  @Nullable
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Calendar calendar = new GregorianCalendar();

    DatePickerDialog dialog = new DatePickerDialog(mActivity, R.style.AppCompatTheme, new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mActivity.setDate(year, monthOfYear, dayOfMonth);
        mActivity.switchFragments(mActivity.mPickTimeFragment);
        dismiss();
      }
    },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    );

    return dialog;
  }
}
