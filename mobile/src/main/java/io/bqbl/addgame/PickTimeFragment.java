package io.bqbl.addgame;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.bqbl.R;

/**
 * Created by sam on 9/3/2015.
 */
public class PickTimeFragment extends DialogFragment {
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

    TimePickerDialog dialog = new TimePickerDialog(mActivity,
        R.style.AppCompatTheme,
        new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mActivity.setTime(hourOfDay, minute);
            mActivity.switchToPlacePicker();
            mActivity.hideFragments();
            dismiss();
          }
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false);

    return dialog;
  }
}
