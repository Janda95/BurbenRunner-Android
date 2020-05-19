package com.jlrutilities.burbenrunner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class MarkerHistoryItem {

  private String typeString;
  private int position;
  private Marker marker;
  private List<Marker> markerSnapshot;

  /*
  *  Types:
  *  1. NewMarker
  *  2. MoveMarker
  *  3. Clear
  *
  *  Exceptions:
  *   Clear will delete Stack and Markers
  *
  *  Create factory object? with general typeString in the history stack
  * */

  MarkerHistoryItem(){}

  // Clear Markers
  MarkerHistoryItem(int type, List<Marker> markerList){
    setTypeString(type);
    this.position = -1;
    this.marker = null;
    this.markerSnapshot = markerList;
  }

  // New Marker
  MarkerHistoryItem(int type, Marker marker, int position){
    setTypeString(type);
    this.position = position;
    this.markerSnapshot = null;
    this.marker = marker;
  }


  public void setTypeString(int typeNum) {

    switch (typeNum) {
      case 1:
        typeString = "NewMarker";
        break;
      case 2:
        typeString = "MoveMarker";
        break;
      case 3:
        typeString = "Clear";
        break;
      default:
        break;

    }
    this.typeString = typeString;
  }


  public String getTypeString(){
    return typeString;
  }

  public int getPosition(){
    return position;
  }

  public List<Marker> getMarkerList() {
    return markerSnapshot;
  }

  public Marker getMarker() {
    return marker;
  }
}
