package com.jlrutilities.burbenrunner;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

  private final String TAG = "ListActivity";

  // Recycler View
  private RecyclerView recyclerView;
  //private DummyContent dummyContent;
  private RecyclerView.Adapter adapter;
  RouteFragment.OnListFragmentInteractionListener listener;

  private List<String> exampleList;
  private List<Integer> exampleNumbers;


  // Database
  RouteDatabaseHelper mDatabaseHelper;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    exampleList = new ArrayList<>();
    for(int i = 0; i < 9; i++){
      exampleList.add("Number: " + i + " ");
    }

    for(int i = 10; i < 19; i++){
      exampleNumbers.add(i);
    }

    listener = new RouteFragment.OnListFragmentInteractionListener() {
      @Override
      public void onListFragmentInteraction(int id, int position, String name) {

      }
    };

    // Recycler View with Adapter
    recyclerView = findViewById(R.id.include_list_fragment);
    adapter = new MyRouteRecyclerViewAdapter(exampleList, exampleNumbers, listener);
    recyclerView.setAdapter(adapter);

    // Database
    mDatabaseHelper = new RouteDatabaseHelper(this);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.list_options, menu);
    return true;
  }

  public void aaaaaddData(String newEntry){
    boolean insertData = mDatabaseHelper.addNewTable(newEntry);
    if (insertData) {
      toastMessage("Data Inserted Successfully!");
    } else {
      toastMessage("Something went wrong");
    }
  }

  public void addData(MenuItem item){
    String newEntry = "New Item";
    boolean insertData = mDatabaseHelper.addNewTable(newEntry);
    if (insertData) {
      toastMessage("Data Inserted Successfully!");
    } else {
      toastMessage("Something went wrong");
    }
  }

  public void clearData(MenuItem item){
    //String newEntry = "New Item";
    /*boolean clearData = mDatabaseHelper.clearTable();
    if (clearData) {
      toastMessage("Data Inserted Successfully!");
    } else {
      toastMessage("Something went wrong");
    }*/
  }

  private void populateListView(){
    Log.d(TAG, "populateListView: Displaying data in the ListView.");
  }

  private void toastMessage(String message){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
