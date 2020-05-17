package com.jlrutilities.burbenrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeletionConfirmDialogFragment extends DialogFragment {

  private static final String MAP_ID_KEY = "map_id_key";
  private int mMapId;

  DeletionConfirmDialogListener listener;

  public interface  DeletionConfirmDialogListener{
    void onDeletionConfirmPositiveClick(DialogFragment dialog, int mapId);
  }


  public DeletionConfirmDialogFragment(){}


  public static DeletionConfirmDialogFragment newInstance(int mapId){
    Bundle args = new Bundle();
    args.putInt(MAP_ID_KEY, mapId);

    DeletionConfirmDialogFragment fragment = new DeletionConfirmDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public void onAttach(Context context){
    super.onAttach(context);

    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the DialogListener so we can send events to the host
      listener = (DeletionConfirmDialogListener) context;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(getActivity().toString()
          + " must implement RequestSaveDialogListener");
    }
  }


  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

    Bundle args = getArguments();
    mMapId = args.getInt(MAP_ID_KEY);
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
            listener.onDeletionConfirmPositiveClick(DeletionConfirmDialogFragment.this, mMapId);
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