package com.jlrutilities.burbenrunner;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jlrutilities.burbenrunner.RouteFragment.OnListFragmentInteractionListener;

import java.util.List;


public class MyRouteRecyclerViewAdapter extends RecyclerView.Adapter<MyRouteRecyclerViewAdapter.ViewHolder> {

  private final OnListFragmentInteractionListener mListener;

  private final List<String> routesValues;
  private final List<Integer> routesDBIds;


  public MyRouteRecyclerViewAdapter(List<String> list, List<Integer> idList, OnListFragmentInteractionListener listener){
    routesValues = list;
    routesDBIds = idList;
    mListener = listener;
  }

  // inflate the overall view, pass to single viewholder object
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_route, parent, false);
    return new ViewHolder(view);
  }

  // holder set items based on values
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    holder.myString =  routesValues.get(position);
    holder.myDbId = routesDBIds.get(position);

    holder.mIdView.setText("" + position);
    holder.mContentView.setText(routesValues.get(position));
    holder.mContentIdView.setText("" + routesDBIds.get(position));

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mListener) {
          // Notify the active callbacks interface (the activity, if the
          // fragment is attached to one) that an item has been selected.
          mListener.onListFragmentInteraction(position, holder.myDbId, holder.myString);
        }
      }
    });
  }

  // values size
  @Override
  public int getItemCount() {
    return routesValues.size();
  }

  // needed to inflate single view
  public class ViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public final TextView mContentIdView;

    public String myString;
    public Integer myDbId;

    // set local items and set single row view
    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = (TextView) view.findViewById(R.id.item_number);
      mContentView = (TextView) view.findViewById(R.id.content);
      mContentIdView = (TextView) view.findViewById(R.id.content_id);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}
