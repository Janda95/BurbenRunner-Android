package com.jlrutilities.burbenrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/*
* To DO:
* - implement listener in activity
* - create custom view with entry box
* */

public class CreateRouteDialogFragment extends DialogFragment {
  private static final String GREETING_KEY = "";
  private String greeting;
  private String mapName = "";

  // Map validation
  private int maxNameLength = 15;

  //
  private String dialogTitle = "Create new map";
  private String dialogParams = "Input 1-15 characters";
  private String dialogError = "Please put 1-15 characters";

  private TextView titleTv;
  private EditText mapNameETv;
  private TextView parametersTv;

  CreateRouteDialogListener listener;

  public interface CreateRouteDialogListener{
    void onDialogPositiveClick(DialogFragment dialog, String mapName);
    void onDialogNegativeClick(DialogFragment dialog);
  }

  public CreateRouteDialogFragment(){}

  public static CreateRouteDialogFragment newInstance(){
    Bundle args = new Bundle();
    //args.putString(greeting, passedVariable)

    CreateRouteDialogFragment fragment = new CreateRouteDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context){
    super.onAttach(context);

    //Verify that the host activity implements the callback interface
    try {
      // Instantiate the DialogListener so we can send events to the host
      listener = (CreateRouteDialogListener) context;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(getActivity().toString()
          + " must implement CreateRouteDialogListener");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    // greeting = args.getString(GREETING_KEY);
  }

  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View content = inflater.inflate(R.layout.dialog_new_route, null);

    // Custom Dialog fragment for persistence
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setView(content);

    //set text views up
    titleTv = content.findViewById(R.id.dialog_newMapTitle);
    mapNameETv = content.findViewById(R.id.dialog_newMapEntry);
    parametersTv = content.findViewById(R.id.dialog_newMapParams);

    titleTv.setText(dialogTitle);
    parametersTv.setText(dialogParams);

    // setup button interactions
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Create",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            // check if entry box is empty or not within params
            mapName = mapNameETv.getText().toString();
            if (isNameValid(mapName)){
              listener.onDialogPositiveClick(CreateRouteDialogFragment.this, mapName);
            } else {
              //does nothing, need to override default closing behaivor
              //nameNotValid();
            }
          }
        });

    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            listener.onDialogNegativeClick(CreateRouteDialogFragment.this);
          }
        });

    return alertDialog;
  }

  private void nameNotValid(){
    parametersTv.setText(dialogError);
    parametersTv.setTextColor(Color.RED);
  }

  private boolean isNameValid(String mapName){
    if(mapName.length() == 0 || mapName.length() >= maxNameLength+1 ){
      return false;
    }

    return true;
  }
}
