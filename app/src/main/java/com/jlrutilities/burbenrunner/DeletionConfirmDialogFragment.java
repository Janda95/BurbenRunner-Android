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

public class DeletionConfirmDialogFragment extends DialogFragment {

  public DeletionConfirmDialogFragment(){}

  public static DeletionConfirmDialogFragment newInstance(){
    Bundle args = new Bundle();

    DeletionConfirmDialogFragment fragment = new DeletionConfirmDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View content = inflater.inflate(R.layout.dialog_delete_confirmation, null);

    TextView contentTv = content.findViewById(R.id.delete_confirm_content);
    contentTv.setText("Are you sure you would like to delete this route?");

    // Custom Dialog fragment for persistence
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setView(content);


    // setup button interactions
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

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