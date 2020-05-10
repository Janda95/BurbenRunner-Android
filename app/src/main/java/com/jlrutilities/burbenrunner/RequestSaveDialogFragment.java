package com.jlrutilities.burbenrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RequestSaveDialogFragment extends DialogFragment {
  private String content;

  public RequestSaveDialogFragment(){}

  public static RequestSaveDialogFragment newInstance(){
    Bundle args = new Bundle();

    RequestSaveDialogFragment fragment = new RequestSaveDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View content = inflater.inflate(R.layout.dialog_delete_confirmation, null);

    TextView contentTv = content.findViewById(R.id.delete_confirm_content);
    contentTv.setText("Would you like to save unsaved changes?");

    // Custom Dialog fragment for persistence
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setView(content);


    // setup button interactions
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });

    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Skip",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });

    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });

    return alertDialog;
  }

}