package com.jlrutilities.burbenrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ListHelpDialogFragment extends DialogFragment {
  private String content;

  public ListHelpDialogFragment(){}

  public static ListHelpDialogFragment newInstance(){
    Bundle args = new Bundle();

    ListHelpDialogFragment fragment = new ListHelpDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View content = inflater.inflate(R.layout.dialog_list_help, null);

    // Custom Dialog fragment for persistence
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setView(content);


    // setup button interactions
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            // Delete from database

          }
        });

    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });

    return alertDialog;
  }

}
