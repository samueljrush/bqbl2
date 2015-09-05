package io.bqbl.addgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class PickNumTeamsFragment extends DialogFragment {

  public static PickNumTeamsFragment newInstance(int title) {
    PickNumTeamsFragment frag = new PickNumTeamsFragment();
    Bundle args = new Bundle();
    args.putInt("title", title);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("How many teams?");

// Set up the input
    final EditText input = new EditText(getActivity());
    //input.addTextChangedListener(new PositiveIntTextWatcher());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input.setInputType(InputType.TYPE_CLASS_NUMBER);
    input.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
    builder.setView(input);

    return builder.setPositiveButton("Ok",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
           int numTeams = Integer.valueOf(input.getText().toString());
           AddGameActivity mActivity = (AddGameActivity) getActivity();
           mActivity.setNumTeams(numTeams);
            mActivity.switchFragments(mActivity.mPickTeamFragment);
          }
        }
    ).setCancelable(false).create();
  }

  public class PositiveIntTextWatcher implements TextWatcher {
    private CharSequence currentText;
    private boolean shouldPreventChange;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      currentText = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }
}