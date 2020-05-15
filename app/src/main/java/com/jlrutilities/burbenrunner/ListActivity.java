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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

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
      public void onLongClickListFragmentInteraction(int position, int id) {}
    };

    // Listener then populate list
    populateListData();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.list_options, menu);
    return true;
  }


  public void newRoute(MenuItem item){
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

    // Create new adapter
    adapter = new MyRouteRecyclerViewAdapter(listStringData, listIntegerData, listDoubleData, listener);
    recyclerView.setAdapter(adapter);
  }


  private void toastMessage(String message){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
