package com.jlrutilities.burbenrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MapHelpDialogFragment extends DialogFragment {
  private String content;

  public MapHelpDialogFragment(){}

  public static MapHelpDialogFragment newInstance(){
    Bundle args = new Bundle();

    MapHelpDialogFragment fragment = new MapHelpDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View content = inflater.inflate(R.layout.dialog_map_help, null);

    // Custom Dialog fragment for persistence
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setView(content);

    TextView contentTv = content.findViewById(R.id.map_help_content);
    contentTv.setText("This is for information for instructions of map stuff. Pretty cool HUH");

    // setup button interactions
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });

    return alertDialog;
  }

}