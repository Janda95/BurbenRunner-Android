package com.jlrutilities.burbenrunner.Activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.jlrutilities.burbenrunner.Dialogs.MapHelpDialogFragment;
import com.jlrutilities.burbenrunner.Dialogs.RequestSaveDialogFragment;
import com.jlrutilities.burbenrunner.HelperUtils.MarkerHistoryItem;
import com.jlrutilities.burbenrunner.HelperUtils.PermissionUtils;
import com.jlrutilities.burbenrunner.HelperUtils.RouteDatabaseHelper;
import com.jlrutilities.burbenrunner.R;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RequestSaveDialogFragment.RequestSaveDialogListener{

  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  final static int GLOBE_WIDTH = 256;
  final static int ZOOM_MAX = 21;

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
  private int index;
  private double adjDistance;
  private double distance;
  private double oldDistance;

  //intent info
  private int mapId;
  private String mapName;
  private boolean isNewMap;

  // Floating action buttons - FAB
  FloatingActionButton fabMyLocation;
  FloatingActionButton fabSave;
  FloatingActionButton fabClear;
  FloatingActionButton fabUndo;
  FloatingActionButton fabBack;
  FloatingActionButton fabMapHelp;
  FloatingActionButton fabChangeDistanceType;


  // Database
  RouteDatabaseHelper mDatabaseHelper;

  // Marker History
  Deque<MarkerHistoryItem> historyStack;

  // Shared Preferences object
  private SharedPreferences mSharedPref;

  // Name of shared preferences file
  private String sharedPrefFile = "com.jlrutilities.sharedprefs";

  // Key for metric boolean
  private final String MEASUREMENT_KEY = "measure";
  private boolean isMetric;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    // Database and Shared Preferences File
    mDatabaseHelper = new RouteDatabaseHelper(this);
    mSharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

    mapNameEtv = findViewById(R.id.map_name_edit_text_view);

    // Intent Map Information
    Intent intent = getIntent();
    isNewMap = intent.getBooleanExtra("new_map_boolean", true);

    if (isNewMap == true) {
      // New map! Need to create in DB on Save
      mapId = (int) mDatabaseHelper.addNewRoute("");
      oldDistance = 0.00;
      adjDistance = 0.00;
      mapName = "";
      mapNameEtv.setText(mapName);

    } else {
      // Already established map
      mapId = intent.getIntExtra("map_id", -1);
      oldDistance = intent.getDoubleExtra("map_distance", 0.00);
      adjDistance = intent.getDoubleExtra("map_distance", 0.00);
    }

    mapName = intent.getStringExtra("map_name");
    mapNameEtv.setText(mapName);

    // default settings
    isMetric = mSharedPref.getBoolean(MEASUREMENT_KEY, true);
    adjDistance = 0.00;
    index = -1;

    historyStack = new ArrayDeque<>();

    //MapFragment mapFragment = MapFragment.newInstance();
    mapFragment = MapFragment.newInstance();
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
          InputMethodManager imm =
              (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(),
              0);
          mapNameEtv.clearFocus();
          return true;
        } else {
          return false;
        }
      }
    });

    // FAB
    fabMyLocation = findViewById(R.id.fab_my_location);
    fabSave = findViewById(R.id.fab_save);
    fabClear = findViewById(R.id.fab_clear_all);
    fabUndo = findViewById(R.id.fab_undo);
    fabBack = findViewById(R.id.fab_back);
    fabMapHelp = findViewById(R.id.fab_map_help);
    fabChangeDistanceType = findViewById(R.id.fab_mi_km_switch);

    changeDistanceFabImage();

    fabMyLocation.setOnClickListener(view -> {
      // Check Permissions
      if(mPermissionDenied == false) { enableMyLocation(); }

      getLastLocation();
    });

    fabSave.setOnClickListener(view -> {
      saveMapMarkers();

      // Go back to List Activity
      finish();
    });

    fabClear.setOnClickListener(view -> {
      // Clear all markers
      if(markers.size() != 0) {
        MarkerHistoryItem item = new MarkerHistoryItem(3,  markers);
        historyStack.push(item);
        removeEverything();
      }
    });

    fabUndo.setOnClickListener(view -> {
      if(historyStack.size() > 0){
        MarkerHistoryItem historyItem = historyStack.pop();
        handleHistoryItem(historyItem);
      }
    });

    fabBack.setOnClickListener(view -> {
      /*
      If the marker ArrayList do not match the list<marker> clone made in OnCreate request user
      if they want to save changes
      */
      if (originalMarkers.equals(markers)){
        finish();
      } else {
        RequestSaveDialogFragment dialogFragment = RequestSaveDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "Map_Do_Not_Save_Confirm_Fragment");
        // Dialog listener handles response
      }
    });

    fabMapHelp.setOnClickListener(view -> {
      MapHelpDialogFragment helpDialog = MapHelpDialogFragment.newInstance();
      helpDialog.show(getSupportFragmentManager(), "Map_Help_Dialog_Fragment");
    });

    fabChangeDistanceType.setOnClickListener(view -> {
      if(isMetric){
        isMetric = false;
      } else {
        isMetric = true;
      }
      calculateDistance();
      changeDistanceFabImage();
    });
  }


  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mUiSettings = mMap.getUiSettings();

    // Marker Interaction Listeners
    mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
      @Override
      public void onMapLongClick(LatLng latLng) {}
    });

    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng latLng) {
        addMarker(latLng.latitude, latLng.longitude, true);
      }
    });

    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      public boolean onMarkerClick(Marker marker) {
        addMarker(marker.getPosition().latitude , marker.getPosition().longitude + 0.001,
            true);
        // Event was handled by our code do not launch default behaviour.
        return true;
      }
    });

    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
      @Override
      public void onMarkerDragStart(Marker marker) {
        // set index on beginning of drag for replacing marker on drag end
        marker.hideInfoWindow();
        index = markers.indexOf(marker);
        MarkerOptions options;
        if (index == 0) {
          options = new MarkerOptions()
              .position(marker.getPosition())
              .draggable(true)
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_green_marker_24))
              .anchor(0.5F,0.5F);
        } else {
          options = new MarkerOptions()
              .position(marker.getPosition())
              .draggable(true)
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_blue_marker_24))
              .anchor(0.5F,0.5F);
        }

        Marker mMarker = mMap.addMarker(options);
        MarkerHistoryItem item = new MarkerHistoryItem(2, mMarker, index);
        historyStack.push(item);
        mMarker.remove();
      }

      @Override
      public void onMarkerDrag(Marker marker) {}

      @Override
      public void onMarkerDragEnd(Marker marker) {
        // Replace old marker with new marker
        markers.remove(index);
        markers.add(index, marker);

        if(markers.size() > 1){
          calculateDistance();
          updateMultipleLines();
        }
      }
    });

    markers = new ArrayList<>();
    polylines = new ArrayList<>();

    // For finding center of route
    double lowLat = 0;
    double highLat = 0;
    double lowLng = 0;
    double highLng = 0;

    // get route data
    Cursor cursor = mDatabaseHelper.getMarkers(mapId);

    if (cursor.getCount() > 0 && cursor != null) {
      if(cursor.moveToFirst()){
        double lat = cursor.getDouble(2);
        double lng = cursor.getDouble(3);
        lowLat = lat;
        highLat = lat;
        lowLng = lng;
        highLng = lng;

        addMarker(lat, lng, true);
        while (cursor.moveToNext()) {
          lat = cursor.getDouble(2);
          lng = cursor.getDouble(3);
          addMarker(lat, lng, true);

          if (lat > highLat) {
            highLat = lat;
          }

          if (lat < lowLat) {
            lowLat = lat;
          }

          if(lng > highLng) {
            highLng = lng;
          }

          if(lng < lowLng){
            lowLng = lng;
          }
        }
      }
      if (lowLat != 0 && lowLng != 0 && highLat != 0 && highLng != 0) {
        double centerLat = (lowLat + highLat) / 2;
        double centerLng = (lowLng + highLng) / 2;
        LatLng centerOfRoute = new LatLng(centerLat, centerLng);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int zoomLvl = getBoundsZoomLevel(new LatLng(highLat, highLng), new LatLng(lowLat, lowLng),
            dpWidth, dpHeight);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(centerOfRoute, zoomLvl);
        mMap.animateCamera(cameraUpdate);
      }

    }

    // Copy of original markers
    originalMarkers = new ArrayList<>(markers);

    // Check for Location permission
    enableMyLocation();
  }


  @Override
  protected void onPause(){
    super.onPause();
    SharedPreferences.Editor prefEditor = mSharedPref.edit();
    prefEditor.putBoolean(MEASUREMENT_KEY, isMetric);
    prefEditor.apply();
  }


  private void getLastLocation() {
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


  private void addMarker(double lat, double lng, boolean addToStack) {
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

    if (addToStack) {
      MarkerHistoryItem item = new MarkerHistoryItem(1, markers.get(markers.size()-1),
          markers.size() -1);
      historyStack.push(item);
    }

    redrawLines();
  }


  private void addMarkerAtIndex(double lat, double lng, boolean addtostack, int index) {
    LatLng latLng = new LatLng(lat, lng);
    MarkerOptions options;

    // First marker is green, then blue for sequential items
    if (index == 0){
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

    markers.remove(index).remove();
    markers.add(index, mMap.addMarker(options));

    MarkerHistoryItem item = new MarkerHistoryItem(1, markers.get(markers.size()-1),
        markers.size() -1);

    if (addtostack){
      historyStack.push(item);
    }

    redrawLines();
  }


  private void redrawLines(){
    if(markers.size() > 1){
      calculateDistance();
      updateMultipleLines();
    } else if (markers.size() == 1){
      removePolyLines();
    }
    calculateDistance();
  }


  private void handleHistoryItem(MarkerHistoryItem historyItem) {

    int position;
    switch(historyItem.getTypeString()){
      case "NewMarker":
        position = historyItem.getPosition();
        Marker newMarker = markers.get(position);
        newMarker.remove();
        markers.remove(newMarker);

        redrawLines();
        break;
      case "MoveMarker":
        position = historyItem.getPosition();
        Marker mMarker = historyItem.getMarker();

        addMarkerAtIndex(mMarker.getPosition().latitude, mMarker.getPosition().longitude,
            false, position);

        break;
      case"Clear":
        List<Marker> list = historyItem.getMarkerList();
        markers.clear();
        removeEverything();

        for (int i = 0; i < list.size(); i++){
          addMarker(list.get(i).getPosition().latitude, list.get(i).getPosition().longitude,
              false);
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

    // Alter Route if values changed
    if (mapName != routeName || oldDistance != adjDistance) {
      mDatabaseHelper.changeRouteInfo(routeName, adjDistance, mapId);
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
    adjDistance = finalResult;
    setInfoBox(finalResult);
  }


  private void setInfoBox(double dist){
    DecimalFormat df = new DecimalFormat("#.##");
    if (isMetric) {
      double metersToKm = dist * 0.001;
      tvInfo.setText(String.format( " Distance: \n%.2f km", metersToKm));
    } else {
      double metersToMiles = dist * 0.00062137;
      tvInfo.setText(String.format( " Distance: \n%.2f mi", metersToMiles));
    }
  }


  private void removeEverything() {
    // Reset marker and polyline ArrayList
    for(Marker marker : markers){
      marker.remove();
    }
    markers = new ArrayList<>();

    for(Polyline polyline : polylines){
      polyline.remove();
    }
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


  private void updateMultipleLines(){
    removePolyLines();

    for (int i = 1; i < markers.size(); i++){
      PolylineOptions options = new PolylineOptions()
          .add(markers.get(i-1).getPosition())
          .add(markers.get(i).getPosition())
          .color(Color.RED)
          .width(15);

      polylines.add(mMap.addPolyline(options));
    }
  }


  private void toastMessage(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }


  @Override
  public void onRequestDialogPositiveClick(DialogFragment dialog) {
    dialog.dismiss();

    saveMapMarkers();

    finish();
  }


  @Override
  public void onRequestDialogNegativeClick(DialogFragment dialog) {
    dialog.dismiss();

    finish();
  }


  private void enableMyLocation() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      if (mMap != null) {
        mMap.setMyLocationEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);
      }
    } else {
      // Permission to access the location is missing. Show rationale and request permission
      PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
          Manifest.permission.ACCESS_FINE_LOCATION, true);
    }
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
      return;
    }

    if (PermissionUtils.isPermissionGranted(permissions, grantResults,
        Manifest.permission.ACCESS_FINE_LOCATION)) {
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


  public static int getBoundsZoomLevel(LatLng northeast,LatLng southwest, float width,
                                       float height) {
    double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
    double lngDiff = northeast.longitude - southwest.longitude;
    double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
    double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
    double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
    double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX);
    return (int)(zoom);
  }


  private static double latRad(double lat) {
    double sin = Math.sin(lat * Math.PI / 180);
    double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
    return Math.max(Math.min(radX2, Math.PI), - Math.PI) / 2;
  }


  private static double zoom(double mapPx, double worldPx, double fraction) {
    final double LN2 = .693147180559945309417;
    return (Math.log(mapPx / worldPx / fraction) / LN2);
  }


  private void changeDistanceFabImage() {
    if(isMetric){
      fabChangeDistanceType.setImageResource(R.mipmap.text_distance_mi_white);
    } else {
      fabChangeDistanceType.setImageResource(R.mipmap.text_distance_km_white);
    }
  }
}