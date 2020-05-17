package com.jlrutilities.burbenrunner;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RequestSaveDialogFragment.RequestSaveDialogListener{

  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  private boolean mPermissionDenied = false;

  private GoogleMap mMap;
  private MapFragment mapFragment;

  private UiSettings mUiSettings;
  private FusedLocationProviderClient fusedLocationClient;

  private List<Polyline> polylines;
  private List<Marker> markers;
  private List<Marker> originalMarkers;

  TextView tvInfo;
  EditText mapNameEtv;
  private int index = 0;
  private boolean isMetric;
  private double distance;

  //intent info
  private int listPosition;
  private int mapId;
  private String mapName;
  private boolean isNewMap;

  // Database
  RouteDatabaseHelper mDatabaseHelper;

  // Marker History
  Deque<MarkerHistoryItem> historyStack;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    // Database
    mDatabaseHelper = new RouteDatabaseHelper(this);

    mapNameEtv = findViewById(R.id.map_name_edit_text_view);

    // Intent Map Information
    Intent intent = getIntent();
    isNewMap = intent.getBooleanExtra("isNewMap", true);

    if (isNewMap == true){
      // New map! Need to create in DB on Save
      listPosition = -1;
      mapId = -1;
      mapName = "";

    } else {
      // Already established map
      listPosition = intent.getIntExtra("list_position", -1);
      mapId = intent.getIntExtra("map_id", -1);
      mapName = intent.getStringExtra("map_name");
      mapNameEtv.setText(mapName);
    }

    // default settings
    isMetric = false;
    distance = 0.00;
    historyStack = new ArrayDeque<>();

    markers = new ArrayList<>();
    polylines = new ArrayList<>();

    // get route data
    if(isNewMap == false) {
      Cursor cursor = mDatabaseHelper.getMarkers(mapId);
      if (cursor != null) {
        while (cursor.moveToNext()) {
          addMarker(cursor.getDouble(2), cursor.getDouble(3));
        }
      }
    }


    // Copy of original markers
    originalMarkers = new ArrayList<>(markers);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
    //    .findFragmentById(R.id.map);
    //mapFragment.getMapAsync(this);

    MapFragment mapFragment = MapFragment.newInstance();
    FragmentTransaction fragmentTransaction =
        getFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.map, mapFragment);
    fragmentTransaction.commit();

    mapFragment.getMapAsync(this);

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    // TextView
    tvInfo = findViewById(R.id.info_text_view);
    setInfoBox(0.00);

    mapNameEtv.clearFocus();

    // Enter key press
    mapNameEtv.setOnKeyListener(new View.OnKeyListener() {
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN) {
          InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
          mapNameEtv.clearFocus();
          return true;
        } else {
          return false;
        }
      }
    });

    // FAB
    FloatingActionButton fabMyLocation = findViewById(R.id.fab_my_location);
    FloatingActionButton fabSave = findViewById(R.id.fab_save);
    FloatingActionButton fabClear = findViewById(R.id.fab_clear_all);
    FloatingActionButton fabUndo = findViewById(R.id.fab_undo);
    FloatingActionButton fabBack = findViewById(R.id.fab_back);
    FloatingActionButton fabMapType = findViewById(R.id.fab_map_type);
    FloatingActionButton fabMapHelp = findViewById(R.id.fab_map_help);

    fabMyLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Check Permissions
        if(mPermissionDenied == false) {
          enableMyLocation();
        }

        // Get Last Location
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
              @Override
              public void onSuccess(Location currentLocation) {
                // last location known
                if (currentLocation != null){
                  LatLng latLng = new LatLng(
                      currentLocation.getLatitude(),
                      currentLocation.getLongitude()
                  );

                  CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                  mMap.animateCamera(update);

                } else {
                  toastMessage("Could not connect!");

                }
              }
            });
      }
    });

    fabSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        saveMapMarkers();

        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
      }
    });

    fabClear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Clear all markers
        //MarkerHistoryItem item = new MarkerHistoryItem(3,  markers);
        //historyStack.add(item);
        removeEverything();
      }
    });

    fabUndo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

       // MarkerHistoryItem historyItem = historyStack.pop();
       // toastMessage(historyItem.getMarker().getPosition().toString());
        //if (historyItem != null) {
        //  handleHistoryItem(historyItem);
        //}

      }
    });

    fabBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        /* if marker ArrayList do not match marker clone made in OnCreate ask user if they would
         like to save
        */
        if (originalMarkers.equals(markers)){
          Intent intent = new Intent(getApplicationContext(), ListActivity.class);
          startActivity(intent);
        } else {
          RequestSaveDialogFragment dialogFragment = RequestSaveDialogFragment.newInstance();
          dialogFragment.show(getSupportFragmentManager(), "Map_Do_Not_Save_Confirm_Fragment");
          // Implement Listeners
        }
      }
    });

    fabMapType.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });

    fabMapHelp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MapHelpDialogFragment helpDialog = MapHelpDialogFragment.newInstance();
        helpDialog.show(getSupportFragmentManager(), "Map_Help_Dialog_Fragment");
      }
    });
  }


  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mUiSettings = mMap.getUiSettings();

    // Add a marker in Sydney and move the camera
    LatLng sydney = new LatLng(-34, 151);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
      @Override
      public View getInfoWindow(Marker marker) {
        return null;
      }

      @Override
      public View getInfoContents(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.info_window, null);
        TextView tvLat = view.findViewById(R.id.tvLat);
        TextView tvLng = view.findViewById(R.id.tvLng);

        LatLng latLng = marker.getPosition();
        tvLat.setText("Latitude: " + latLng.latitude);
        tvLng.setText("Longitude: " + latLng.longitude);

        return view;
      }
    });

    // Marker Interaction Listeners
    mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
      @Override
      public void onMapLongClick(LatLng latLng) {
        addMarker(latLng.latitude, latLng.longitude);
      }
    });

    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        String msg = marker.getTitle() + " (" +
            marker.getPosition().latitude + ", "+
            marker.getPosition().longitude+ ")";
        Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();

        return false;
      }
    });

    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
      @Override
      public void onMarkerDragStart(Marker marker) {
        // set index on beginning of drag for replacing marker on drag end
        index = markers.indexOf(marker);
      }

      @Override
      public void onMarkerDrag(Marker marker) {}

      @Override
      public void onMarkerDragEnd(Marker marker) {
        // Replace old marker with new marker

        //MarkerHistoryItem item = new MarkerHistoryItem(2, marker, markers.indexOf(marker));
        //historyStack.add(item);

        markers.remove(index);
        markers.add(index, marker);

        if(markers.size() > 1){
          calculateDistance();
          updateMultipleLines();
        }
      }
    });

    // Check for Location permission
    enableMyLocation();
  }


  private void addMarker(double lat, double lng){

    LatLng latLng = new LatLng(lat, lng);
    MarkerOptions options;

    // First marker is green, then blue for sequential items
    if (markers.size() == 0){
      options = new MarkerOptions()
          .position(latLng)
          .draggable(true)
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_green_marker_24))
          .anchor(0.5F,0.5F);

    } else {
      options = new MarkerOptions()
          .position(latLng)
          .draggable(true)
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_blue_marker_24))
          .anchor(0.5F,0.5F);
    }

    markers.add(mMap.addMarker(options));

    //MarkerHistoryItem item = new MarkerHistoryItem(1, markers.get(markers.size()-1), markers.size() -1);
    //historyStack.add(item);

    if (markers.size() > 1){
      drawMultipleLines();
      calculateDistance();
    }
  }


  private void handleHistoryItem(MarkerHistoryItem historyItem) {
    switch(historyItem.getTypeString()){
      case "NewMarker":
        //int position = historyItem.getPosition();
        Marker newMarker = historyItem.getMarker();
        //markers.remove(position);
        markers.remove(newMarker);
        if(markers.size() > 1){
          calculateDistance();
          updateMultipleLines();
        }
        break;
      case "MoveMarker":
        int position = historyItem.getPosition();
        Marker mMarker = historyItem.getMarker();
        markers.set(position, mMarker);
        if(markers.size() > 1){
          calculateDistance();
          updateMultipleLines();
        }
        break;
      case"Clear":
        List<Marker> list = historyItem.getMarkerList();
        markers.clear();
        for (int i = 0; i < list.size(); i++){
          markers.add(list.get(i));
        }
        if(markers.size() > 1){
          calculateDistance();
          updateMultipleLines();
        }
        break;
    }
  }


  private void saveMapMarkers() {
    String routeName = mapNameEtv.getText().toString();
    if(isNewMap == true){
      mapId = (int) mDatabaseHelper.addNewRoute(routeName);
      isNewMap = false;
    } else {
      if (mapName != routeName) {
        mDatabaseHelper.changeRouteName(routeName, mapId);
      }
    }

    // Save marker info to db as map
    mDatabaseHelper.clearMarkers(mapId);
    Cursor cursor = mDatabaseHelper.getRouteWithId(mapId);
    if(cursor.moveToFirst() == false){
      toastMessage("Route does not exist in DB");
      return;
    }

    for( int i = 0; i < markers.size(); i++){
      Marker marker = markers.get(i);
      LatLng position = marker.getPosition();
      mDatabaseHelper.saveMarker(i, position.latitude, position.longitude, mapId);
    }
  }


  private void calculateDistance() {
    float[] result = new float[1];
    double finalResult = 0.0;

    for( int i = 1; i < markers.size(); i++){
      LatLng one = markers.get(i-1).getPosition();
      LatLng two = markers.get(i).getPosition();
      Location.distanceBetween(one.latitude, one.longitude, two.latitude, two.longitude, result);
      finalResult += result[0];
    }

    distance = finalResult;
    setInfoBox(finalResult);
  }


  private void setInfoBox(double dist){
    if (isMetric) {
      double metersToKm = dist * 0.001;
      tvInfo.setText(String.format( "Distance: %.2f mi", metersToKm));

    } else {
      double metersToMiles = dist * 0.00062137;
      tvInfo.setText(String.format( "Distance: %.2f mi", metersToMiles));
    }

  }


  private void removeEverything() {
    // Multiple Lines
    for(Marker marker : markers){
      marker.remove();
    }
    //markers.clear();
    markers = new ArrayList<>();

    for(Polyline polyline : polylines){
      polyline.remove();
    }
    // polylines.clear();
    polylines = new ArrayList<>();

    calculateDistance();
  }


  private void removePolyLines(){
    for(Polyline polyline : polylines){
      polyline.remove();
    }
    polylines.clear();
    polylines = new ArrayList<>();
  }


  private void drawMultipleLines(){
    int size = markers.size();
    Marker markerStart = markers.get(size-2);
    Marker markerEnd = markers.get(size-1);

    PolylineOptions options = new PolylineOptions()
        .add(markerStart.getPosition())
        .add(markerEnd.getPosition())
        .color(Color.RED)
        .width(20);

    polylines.add(mMap.addPolyline(options));
  }


  private void updateMultipleLines(){
    removePolyLines();

    for (int i = 1; i < markers.size(); i++){
      PolylineOptions options = new PolylineOptions()
          .add(markers.get(i-1).getPosition())
          .add(markers.get(i).getPosition())
          .color(Color.RED)
          .width(20);

      polylines.add(mMap.addPolyline(options));
    }
  }


  private void toastMessage(String message){
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }


  @Override
  public void onRequestDialogPositiveClick(DialogFragment dialog) {
    dialog.dismiss();

    saveMapMarkers();

    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
    startActivity(intent);
  }


  @Override
  public void onRequestDialogNegativeClick(DialogFragment dialog) {
    dialog.dismiss();

    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
    startActivity(intent);
  }


  private void enableMyLocation() {

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      if (mMap != null) {
        mMap.setMyLocationEnabled(true);
      }
    } else {
      // Permission to access the location is missing. Show rationale and request permission
      PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
          Manifest.permission.ACCESS_FINE_LOCATION, true);
    }
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
      return;
    }

    if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
      // Enable the my location layer if the permission has been granted.
      enableMyLocation();
    } else {
      // Permission was denied. Display an error message
      mPermissionDenied = true;
    }
  }


  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    if (mPermissionDenied) {
      // Permission was not granted, display error dialog.
      showMissingPermissionError();
      mPermissionDenied = false;
    }
  }


  // Displays a dialog with error message explaining that the location permission is missing.
  private void showMissingPermissionError() {
    PermissionUtils.PermissionDeniedDialog
        .newInstance(true).show(getSupportFragmentManager(), "dialog");
  }

}
