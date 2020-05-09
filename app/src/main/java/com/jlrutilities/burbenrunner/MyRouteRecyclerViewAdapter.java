package com.jlrutilities.burbenrunner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jlrutilities.burbenrunner.RouteFragment.OnListFragmentInteractionListener;

import java.util.List;


public class MyRouteRecyclerViewAdapter extends RecyclerView.Adapter<MyRouteRecyclerViewAdapter.ViewHolder> {

  private final OnListFragmentInteractionListener mListener;

  private final List<String> routesValues;
  private final List<Integer> routesDBIds;
  private final List<Double> routesDistances;

  public MyRouteRecyclerViewAdapter(List<String> list, List<Integer> idList, List<Double> distanceList, OnListFragmentInteractionListener listener){
    routesValues = list;
    routesDBIds = idList;
    routesDistances = distanceList;
    mListener = listener;
  }

  // inflate the overall view, pass to single viewholder object
  /*@Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_route, parent, false);
    return new ViewHolder(view);
  }*/

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_route_list_item, parent, false);
    return new ViewHolder(view);
  }

  // holder set items based on values
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    holder.myString =  routesValues.get(position);
    holder.myDbId = routesDBIds.get(position);

    holder.mIdView.setText("Pos: " + position);
    holder.mContentView.setText("Name: " + routesValues.get(position));
    //holder.mDistanceView.setText("" + routesDBIds.get(position));
    holder.mDistanceView.setText("Distance: " + routesDistances.get(position));
    holder.mImageView.setBackgroundResource(R.drawable.baseline_my_location_black_24);

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mListener) {
          // Notify the active callbacks interface (the activity, if the
          // fragment is attached to one) that an item has been selected.
          mListener.onClickListFragmentInteraction(position, holder.myDbId, holder.myString);
        }
      }
    });


    holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View view){
        if (null != mListener){
          mListener.onLongClickListFragmentInteraction(position, holder.myDbId);
          //return true;
        }
        return false;
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
    public final TextView mDistanceView;
    public final ImageView mImageView;

    public String myString;
    public Integer myDbId;

    // set local items and set single row view
    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = (TextView) view.findViewById(R.id.item_number);
      mContentView = (TextView) view.findViewById(R.id.content);
      mDistanceView = (TextView) view.findViewById(R.id.distanceDesc);
      mImageView = (ImageView) view.findViewById(R.id.imageListIcon);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}
