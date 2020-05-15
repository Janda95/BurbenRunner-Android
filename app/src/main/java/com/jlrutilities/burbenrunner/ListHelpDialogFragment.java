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

public class ListHelpDialogFragment extends DialogFragment {
  private TextView contentTv;


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

    contentTv = (TextView) content.findViewById(R.id.list_help_content);
    contentTv.setText("Single Click to Open Map\nLong Click to Delete\nMenu + Button to Create New Route");

    // setup button interactions
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    return alertDialog;
  }
}
