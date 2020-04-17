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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements CreateRouteDialogFragment.CreateRouteDialogListener {

  private final String TAG = "ListActivity";

  // Recycler View
  private RecyclerView recyclerView;
  //private DummyContent dummyContent;
  private RecyclerView.Adapter adapter;
  RouteFragment.OnListFragmentInteractionListener listener;

  private List<String> exampleList;
  private List<Integer> exampleNumbers;

  ArrayList<Integer> listIntegerData;
  ArrayList<String> listStringData;

  // Database
  RouteDatabaseHelper mDatabaseHelper;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Database
    mDatabaseHelper = new RouteDatabaseHelper(this);

    //Main view
    recyclerView = findViewById(R.id.include_list_fragment);


    // Recycler View with Adapter with example
    /*exampleList = new ArrayList<>();
    exampleNumbers = new ArrayList<>();
    for(int i = 0; i < 9; i++){ exampleList.add("Number: " + i + " "); }
    for(int i = 10; i < 19; i++){ exampleNumbers.add(i); }*/

    //adapter = new MyRouteRecyclerViewAdapter(exampleList, exampleNumbers, listener);
    //recyclerView.setAdapter(adapter);

    // my own version
    listIntegerData = new ArrayList<>();
    listStringData = new ArrayList<>();

    listener = new RouteFragment.OnListFragmentInteractionListener() {
      @Override
      public void onListFragmentInteraction(int position, int id, String name) {
        //intent!
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("list_position", position);
        intent.putExtra("map_id", id);
        intent.putExtra("map_name", name);
        startActivity(intent);
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
    CreateRouteDialogFragment dialogFragment = CreateRouteDialogFragment.newInstance();
    dialogFragment.show(getSupportFragmentManager(), "DIALOG_FRAGMENT");
  }

  public void clearData(MenuItem item){
    mDatabaseHelper.clearTables();
    populateListData();
  }

  private void populateListData(){
    Log.d(TAG, "populateListView: Displaying data in the ListView.");

    Cursor data = mDatabaseHelper.getData();
    // walking through cursor getting id and map name
    if(data == null){
      toastMessage("Error: Database Cursor is null");
      return;
    } else {
      listIntegerData.clear();
      listStringData.clear();
      while(data.moveToNext()){
        listIntegerData.add(Integer.valueOf(data.getString(0)));
        listStringData.add(data.getString(1));
      }
    }

    //adapt!!!!
    adapter = new MyRouteRecyclerViewAdapter(listStringData, listIntegerData, listener);
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
