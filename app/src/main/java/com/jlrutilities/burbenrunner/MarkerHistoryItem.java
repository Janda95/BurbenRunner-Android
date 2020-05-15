package com.jlrutilities.burbenrunner;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class MarkerHistoryItem {

  private String type;
  private double lat;
  private double lng;
  private int position;
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
  *  Create factory object? with general type in the history stack
  * */

  MarkerHistoryItem(){}


  MarkerHistoryItem(int type, double lat, double lng, int position, List<Marker> markerList){
    setType(type);
    this.lat = lat;
    this.lng = lng;
    this.position = position;
    this.markerSnapshot = markerList;
  }


  public void setType(int typeNum) {

    switch (typeNum) {
      case 1:
        type = "NewMarker";
        break;
      case 2:
        type = "MoveMarker";
        break;
      case 3:
        type = "Clear";
        break;
      default:
        break;

    }

    this.type = type;
  }


  public String getType(){
    return type;
  }


  public double getLat(){
    return lat;
  }


  public double getLng(){
    return lng;
  }


  public int getPosition(){
    return position;
  }

}
