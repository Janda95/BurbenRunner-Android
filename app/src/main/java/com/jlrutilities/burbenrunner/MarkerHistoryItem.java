package com.jlrutilities.burbenrunner;

public class MarkerHistoryItem {

  private String type;
  private double lat;
  private double lng;
  private int position;

  /*
  *  Types:
  *  NewMarker
  *  MovedMarker
  *
  *  Exceptions:
  *   Clear will delete Stack and Markers
  * */

  MarkerHistoryItem(){}

  MarkerHistoryItem(String type, double lat, double lng, int position){
    this.type = type;
    this.lat = lat;
    this.lng = lng;
    this.position = position;
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
