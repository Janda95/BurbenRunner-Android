package com.jlrutilities.burbenrunner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ChangeMapTypeDialogFragment extends DialogFragment {

  public ChangeMapTypeDialogFragment(){}

  public ChangeMapTypeDialogFragment newInstance(){
    Bundle args = new Bundle();

    ChangeMapTypeDialogFragment fragment = new ChangeMapTypeDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


  }
}
