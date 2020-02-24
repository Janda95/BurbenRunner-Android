package com.jlrutilities.burbenrunner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jlrutilities.burbenrunner.dummy.DummyContent;

public class ListActivity extends AppCompatActivity {

  private RecyclerView recyclerView;
  private DummyContent dummyContent;
  private RecyclerView.Adapter adapter;
  RouteFragment.OnListFragmentInteractionListener listener;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    listener = new RouteFragment.OnListFragmentInteractionListener() {
      @Override
      public void onListFragmentInteraction(DummyContent.DummyItem item) {

      }
    };

    recyclerView = findViewById(R.id.include_list_fragment);
    dummyContent = new DummyContent();
    adapter = new MyRouteRecyclerViewAdapter(DummyContent.ITEMS, listener);
    recyclerView.setAdapter(adapter);

  }

}
