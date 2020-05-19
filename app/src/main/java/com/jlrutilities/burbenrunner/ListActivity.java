package com.jlrutilities.burbenrunner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements DeletionConfirmDialogFragment.DeletionConfirmDialogListener {

  private final String TAG = "ListActivity";

  // Recycler View
  private RecyclerView recyclerView;
  private RecyclerView.Adapter adapter;
  RouteFragment.OnListFragmentInteractionListener listener;

  ArrayList<Integer> listIntegerData;
  ArrayList<String> listStringData;
  ArrayList<Double> listDoubleData;

  // Database
  RouteDatabaseHelper mDatabaseHelper;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Database
    mDatabaseHelper = new RouteDatabaseHelper(this);

    //Main view
    recyclerView = findViewById(R.id.include_list_fragment);

    // my own version
    listIntegerData = new ArrayList<>();
    listStringData = new ArrayList<>();
    listDoubleData = new ArrayList<>();

    listener = new RouteFragment.OnListFragmentInteractionListener() {
      @Override
      public void onClickListFragmentInteraction(int position, int id, String name, double myDistance) {
        // Transition to MapsActivity
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("new_map_boolean", false);
        intent.putExtra("map_id", id);
        intent.putExtra("map_name", name);
        intent.putExtra("map_distance", myDistance);
        startActivity(intent);
      }

      @Override
      public void onLongClickListFragmentInteraction(int position, int id) {
        DeletionConfirmDialogFragment dialogFragment = DeletionConfirmDialogFragment.newInstance(listIntegerData.get(position));
        dialogFragment.show(getSupportFragmentManager(), "delete_route_dialog_fragment");
      }
    };
  }


  /*
  *   Populating List data in onResume so it is always called if ListActivity is destroyed or paused
  * on transition to Map Activity and back
  * */
  @Override
  protected void onResume(){
    super.onResume();
    populateListData();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.list_options, menu);
    return true;
  }


  public void newRoute(MenuItem item){
    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
    intent.putExtra("new_map_boolean", true);
    intent.putExtra("map_id", -1);
    intent.putExtra("map_name", "");
    intent.putExtra("map_distance", 1.00);
    startActivity(intent);
  }


  public void displayListHelpDialog(MenuItem item){
    ListHelpDialogFragment dialogFragment = ListHelpDialogFragment.newInstance();
    dialogFragment.show(getSupportFragmentManager(), "LIST_DIALOG_FRAGMENT");
  }


  private void populateListData(){
    Cursor data = mDatabaseHelper.getRoutes();

    // walking through cursor getting map information
    if(data == null){
      toastMessage("Error: Database Cursor is null");
      return;
    } else {
      listIntegerData.clear();
      listStringData.clear();
      listIntegerData.clear();
      while(data.moveToNext()){
        listIntegerData.add(Integer.valueOf(data.getString(0)));
        listStringData.add(data.getString(1));
        listDoubleData.add(data.getDouble(2));
      }
    }

    // Create new adapter
    adapter = new MyRouteRecyclerViewAdapter(listStringData, listIntegerData, listDoubleData, listener);
    recyclerView.setAdapter(adapter);
  }


  private void toastMessage(String message){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }


  @Override
  public void onDeletionConfirmPositiveClick(DialogFragment dialog, int mapId) {
    mDatabaseHelper.deleteRoute(mapId);
    populateListData();
    toastMessage("Deleted Route");
  }
}
