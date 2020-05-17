package com.jlrutilities.burbenrunner;

import android.content.Context;
import android.database.Cursor;

import androidx.test.core.app.ApplicationProvider;

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
  public void database_getSingleRoute(){
    String mapName = "NewRoute_KEY";
    databaseHelper.addNewRoute(mapName);
    Cursor cursor = databaseHelper.getRoutesWithName(mapName);
    cursor.moveToFirst();
    String result = cursor.getString(1);
    assertEquals(result, mapName);
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

  /*@Test
  public void database_(){
    assertEquals(,);
  }*/

  @Test
  public void addition_isCorrect() {
    assertEquals(4, 2 + 2);
  }
}