package com.jlrutilities.burbenrunner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements CreateRouteDialogFragment.CreateRouteDialogListener {

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
      public void onClickListFragmentInteraction(int position, int id, String name) {
        // Transition to MapsActivity
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("isNewMap", false);
        intent.putExtra("list_position", position);
        intent.putExtra("map_id", id);
        intent.putExtra("map_name", name);
        startActivity(intent);
      }

      @Override
      public void onLongClickListFragmentInteraction(int position, int id) {
        // Ask user if they want to delete the route then delete or leave alone

      }
    };

    // listener then populate
    populateListData();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.list_options, menu);
    return true;
  }

  public void addNewRoute(String newEntry){

    boolean insertData = mDatabaseHelper.addNewTable(newEntry);
    if (insertData) {
      toastMessage("Data Inserted Successfully!");
    } else {
      toastMessage("Something went wrong");
    }
    populateListData();
  }

  public void newRoute(MenuItem item){
    //CreateRouteDialogFragment dialogFragment = CreateRouteDialogFragment.newInstance();
    //dialogFragment.show(getSupportFragmentManager(), "DIALOG_FRAGMENT");
    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
    intent.putExtra("isNewMap", true);
    startActivity(intent);
  }

  public void displayListHelpDialog(MenuItem item){
    ListHelpDialogFragment dialogFragment = ListHelpDialogFragment.newInstance();
    dialogFragment.show(getSupportFragmentManager(), "LIST_DIALOG_FRAGMENT");
  }

  private void populateListData(){
    Log.d(TAG, "populateListView: Displaying data in the ListView.");

    Cursor data = mDatabaseHelper.getRoutes();
    // walking through cursor getting id and map name
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

    //adapt!!!!
    adapter = new MyRouteRecyclerViewAdapter(listStringData, listIntegerData, listDoubleData, listener);
    recyclerView.setAdapter(adapter);
  }

  private void toastMessage(String message){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onDialogPositiveClick(DialogFragment dialog, String mapName) {
    dialog.dismiss();
    addNewRoute(mapName);
  }

  @Override
  public void onDialogNegativeClick(DialogFragment dialog) {
    // close dialog
    dialog.dismiss();
  }
}
