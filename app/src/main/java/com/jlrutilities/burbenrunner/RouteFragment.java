package com.jlrutilities.burbenrunner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RouteFragment extends Fragment {

  private static final String ARG_COLUMN_COUNT = "column-count";

  private int mColumnCount = 1;
  private OnListFragmentInteractionListener mListener;
  private List<String> exampleList;
  private List<Integer> exampleNumbers;
  private List<Double> exampleDistance;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public RouteFragment() {}


  public static RouteFragment newInstance(int columnCount) {
    RouteFragment fragment = new RouteFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_COLUMN_COUNT, columnCount);
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    exampleList = new ArrayList<>();
    exampleDistance = new ArrayList<>();

    for(int i = 10; i < 12; i++) {
      exampleList.add("Number: " + i + " ");
      exampleNumbers.add(i);
      exampleDistance.add(i * 1.12);
    }

    if (getArguments() != null) {
      mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
    }
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_route_list, container, false);

    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = (RecyclerView) view;
      if (mColumnCount <= 1) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
      } else {
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
      }
      recyclerView.setAdapter(new MyRouteRecyclerViewAdapter(exampleList, exampleNumbers, exampleDistance, mListener));
    }
    return view;
  }


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnListFragmentInteractionListener) {
      mListener = (OnListFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnListFragmentInteractionListener");
    }
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }


  public interface OnListFragmentInteractionListener {
    // TODO: Update argument type and name
    void onClickListFragmentInteraction(int position, int id, String name, double myDistance);
    void onLongClickListFragmentInteraction(int position, int id);
  }
}
