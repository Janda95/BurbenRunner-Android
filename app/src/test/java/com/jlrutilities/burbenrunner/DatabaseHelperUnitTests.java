package com.jlrutilities.burbenrunner;

import android.content.Context;
import android.database.Cursor;

import androidx.test.core.app.ApplicationProvider;

import com.jlrutilities.burbenrunner.HelperUtils.RouteDatabaseHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

/*
*  assertEquals(4, 2 + 2);
*  assertTrue(databaseHelper.isOpen());
* */

@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperUnitTests {
  private RouteDatabaseHelper databaseHelper;

  @Before
  public void createDb(){
    Context context = ApplicationProvider.getApplicationContext();

    databaseHelper = new RouteDatabaseHelper(context);
  }

  @After
  public void closeDb() throws IOException {
    databaseHelper.clearDatabase();
    databaseHelper.close();
  }


  @Test
  public void database_isOpen() {
    assertTrue(databaseHelper.isOpen());
  }


  @Test
  public void database_clearTables(){

    databaseHelper.addNewRoute("newRouteTest");
    Cursor aCursor = databaseHelper.getRoutes();
    assertEquals(aCursor.getCount() > 0, true);

    databaseHelper.clearDatabase();
    Cursor cursor = databaseHelper.getRoutes();
    assertEquals(cursor.getCount(),0);
  }

  @Test
  public void database_addNewRoute(){
    databaseHelper.clearDatabase();
    String mapName = "NewRoute_KEY";
    int row = (int) databaseHelper.addNewRoute(mapName);
    assertTrue(row != -1);
  }


  @Test
  public void database_getSingleRoute_WithName(){
    String mapName = "NewRoute_KEY";
    databaseHelper.addNewRoute(mapName);
    Cursor cursor = databaseHelper.getRoutesWithName(mapName);
    cursor.moveToFirst();
    String result = cursor.getString(1);
    assertEquals(result, mapName);
  }


  @Test
  public void database_getSingleRoute_WithRowId(){
    String mapName = "NewRoute_KEY";
    int row = (int) databaseHelper.addNewRoute(mapName);
    Cursor cursor = databaseHelper.getRouteWithId(row);
    cursor.moveToFirst();
    int result = cursor.getInt(0);
    assertEquals(result, row);
  }


  @Test
  public void database_getRoutes(){
    databaseHelper.clearDatabase();

    String mapName = "UniqueRouteONE";
    String mapNameTwo = "UniqueRouteTwo";
    databaseHelper.addNewRoute(mapName);
    databaseHelper.addNewRoute(mapNameTwo);

    Cursor cursor = databaseHelper.getRoutes();

    cursor.moveToFirst();
    String result = cursor.getString(1);
    assertEquals(result, mapName);

    cursor.moveToNext();
    result = cursor.getString(1);
    assertEquals(result, mapNameTwo);
  }


  @Test
  public void database_addNewMarker(){
    databaseHelper.clearDatabase();
    String mapName = "RouteForNewMarkerTest";
    int routeRow = (int) databaseHelper.addNewRoute(mapName);

    int markerRow = (int) databaseHelper.saveMarker(0, 0.00, 0.00, routeRow);

    assertTrue(markerRow != -1);
  }


  @Test
  public void database_getAllMarkers() {
    databaseHelper.clearDatabase();
    int routeRow = (int) databaseHelper.addNewRoute("a_cool_map");

    databaseHelper.saveMarker(0, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(1, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(2, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(3, 0.00, 0.00, routeRow);

    Cursor cursor = databaseHelper.getMarkers(routeRow);

    assertEquals(cursor.getCount(), 4);
  }


  @Test
  public void database_deleteRoute() {
    databaseHelper.clearDatabase();
    int routeRow = (int) databaseHelper.addNewRoute("delete_route");

    int rowsAffected = databaseHelper.deleteRoute(routeRow);

    assertEquals(rowsAffected, 1);
  }


  @Test
  public void database_deleteRoute_withMarkers() {
    databaseHelper.clearDatabase();
    int routeRow = (int) databaseHelper.addNewRoute("delete_route");

    databaseHelper.saveMarker(0, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(1, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(2, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(3, 0.00, 0.00, routeRow);

    Cursor before = databaseHelper.getMarkers(routeRow);

    databaseHelper.deleteRoute(routeRow);

    Cursor after = databaseHelper.getMarkers(routeRow);

    assertTrue(before.getCount() != after.getCount());
    assertEquals(after.getCount(), 0);
  }


  @Test
  public void database_clearMarkersWithMapId() {
    databaseHelper.clearDatabase();
    int routeRow = (int) databaseHelper.addNewRoute("delete_markers");

    databaseHelper.saveMarker(0, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(1, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(2, 0.00, 0.00, routeRow);
    databaseHelper.saveMarker(3, 0.00, 0.00, routeRow);

    Cursor before = databaseHelper.getMarkers(routeRow);
    databaseHelper.clearMarkers(routeRow);
    Cursor after = databaseHelper.getMarkers(routeRow);

    assertEquals(after.getCount(), 0);
  }


  @Test
  public void database_changeRouteName() {
    databaseHelper.clearDatabase();
    String nameBefore = "Before";
    String nameAfter = "After";
    int routeRow = (int) databaseHelper.addNewRoute(nameBefore);

    databaseHelper.changeRouteInfo(nameAfter, 0.00, routeRow);

    Cursor cursor = databaseHelper.getRouteWithId(routeRow);
    cursor.moveToFirst();
    assertTrue(cursor.getString(1) != nameBefore);
    assertEquals(cursor.getString(1), nameAfter);
  }


  @Test
  public void database_changeRouteDistance(){
    databaseHelper.clearDatabase();
    String name = "name";
    int routeRow = (int) databaseHelper.addNewRoute(name);

    databaseHelper.changeRouteInfo(name, 1.11, routeRow);

    Cursor cursor = databaseHelper.getRouteWithId(routeRow);
    cursor.moveToFirst();
    assertTrue(cursor.getDouble(2) != 0.00);
    assertTrue(cursor.getDouble(2) == 1.11);
  }

}