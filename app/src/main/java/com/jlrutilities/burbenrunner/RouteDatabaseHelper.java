package com.jlrutilities.burbenrunner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

public class RouteDatabaseHelper extends SQLiteOpenHelper {

  // TAG
  private static final String TAG = "DatabaseHelper";

  // Database Info
  private static final String DATABASE_NAME = "RoutesDB";
  private static final int DATABASE_VERSION = 2;

  // Table Names
  private static final String TABLE_ROUTES = "routes";
  private static final String TABLE_MARKERS = "markers";

  // Routes Table Columns
  private static final String KEY_ROUTE_ID = "route_id";
  private static final String KEY_ROUTE_NAME = "route_name";
  private static final String KEY_ROUTE_DISTANCE = "total_distance";

  // Markers Table Columns
  private static final String KEY_MARKER_ID = "marker_id";
  private static final String KEY_MARKER_ORDER = "sequenceposition";
  private static final String KEY_MARKER_LAT = "latitude";
  private static final String KEY_MARKER_LONG = "longitude";

  // Marker Table Route Foreign Key
  private static final String FK_MARKER_MAP_ID = "route_fk";


  public RouteDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }


  // Called when the database connection is being configured.
  // Configure database settings for things like foreign key support, write-ahead logging, etc.
  @Override
  public void onConfigure(SQLiteDatabase db) {
    super.onConfigure(db);
    db.setForeignKeyConstraintsEnabled(true);
  }


  // Called when the database is created for the FIRST time.
  // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_ROUTES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTES +
        "(" +
        KEY_ROUTE_ID + " INTEGER PRIMARY KEY, " +
        KEY_ROUTE_NAME + " TEXT, " +
        KEY_ROUTE_DISTANCE + " DOUBLE" +
        ")";

    String CREATE_MARKERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MARKERS +
        "(" +
        KEY_MARKER_ID + " INTEGER PRIMARY KEY, " +
        KEY_MARKER_ORDER + " INTEGER, " +
        KEY_MARKER_LAT + " DECIMAL(9,6), " +
        KEY_MARKER_LONG + " DECIMAL(9,6), " +
        FK_MARKER_MAP_ID + " Integer, " +
          "FOREIGN KEY "  + "(" + FK_MARKER_MAP_ID + ") " +
          "REFERENCES " + TABLE_ROUTES + "(" + KEY_ROUTE_ID + ") " +
        ")";

    db.execSQL(CREATE_ROUTES_TABLE);
    db.execSQL(CREATE_MARKERS_TABLE);
  }


  // Called when the database needs to be upgraded.
  // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
  // but the DATABASE_VERSION is different than the version of the database that exists on disk.
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion != newVersion) {
      // Simplest implementation is to drop all old tables and recreate them
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);

      onCreate(db);
    }
  }


  public void clearDatabase(){
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_MARKERS + " WHERE 1=1");
    db.execSQL("DELETE FROM " + TABLE_ROUTES + " WHERE 1=1");

  }


  // Example setting value in db
  public long addNewRoute(String item) {
    SQLiteDatabase db  = this.getWritableDatabase();

    ContentValues contentValues = new ContentValues();
    contentValues.put(KEY_ROUTE_NAME, item);
    contentValues.put(KEY_ROUTE_DISTANCE, 0.00);

    // Attempt to add it to DB and check
    long result =  db.insert(TABLE_ROUTES, null, contentValues);

    return result;
  }


  public Cursor getRoutes(){
    SQLiteDatabase db  = this.getReadableDatabase();
    String query = "SELECT * FROM " + TABLE_ROUTES;
    Cursor data = db.rawQuery(query, null);

    return data;
  }


  public Cursor getRoutesWithName(String mapName){
    SQLiteDatabase db = this.getReadableDatabase();
    String query = "SELECT * FROM " + TABLE_ROUTES + " WHERE " + KEY_ROUTE_NAME + "=?";
    String[] args = {mapName};
    Cursor data = db.rawQuery(query, args);

    return data;
  }


  public Cursor getRouteWithId(int mapId) {
    SQLiteDatabase db = this.getReadableDatabase();
    String query = "SELECT * FROM " + TABLE_ROUTES + " WHERE " + KEY_ROUTE_ID + "=?";
    String[] args = {Integer.toString(mapId)};
    Cursor cursor = db.rawQuery(query, args);

    return cursor;
  }


  public Cursor getMarkers(int mapId){
    SQLiteDatabase db  = this.getReadableDatabase();
    String query = "SELECT * FROM " + TABLE_MARKERS + " WHERE " + FK_MARKER_MAP_ID + " = ? ORDER BY " + KEY_MARKER_ORDER + " ASC";
    String str = "" + mapId;
    String[] args = {str};
    Cursor data = db.rawQuery(query, args);

    return data;
  }


  public int deleteRoute(int id){

    SQLiteDatabase db  = this.getWritableDatabase();

    int markerRowsAffected = db.delete(TABLE_MARKERS, FK_MARKER_MAP_ID + "=" + id, null);
    int routeRowsAffected = db.delete(TABLE_ROUTES, KEY_ROUTE_ID + "=" + id, null);

    return 0;
  }


  public int clearMarkers(int id){
    SQLiteDatabase db  = this.getWritableDatabase();
    int rowsAffected = db.delete(TABLE_MARKERS, FK_MARKER_MAP_ID + "=" + id, null);

    return rowsAffected;
  }


  public long saveMarker(int order, double lat, double lng, int mapId) {
    SQLiteDatabase db  = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(KEY_MARKER_ORDER, order);
    contentValues.put(KEY_MARKER_LAT, lat);
    contentValues.put(KEY_MARKER_LONG, lng);
    contentValues.put(FK_MARKER_MAP_ID, mapId);

    long rowId = db.insert(TABLE_MARKERS, null, contentValues);

    return rowId;
  }


  public int changeRouteName(String routeName, double distance, int mapId) {
    SQLiteDatabase db  = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(KEY_ROUTE_NAME, routeName);
    contentValues.put(KEY_ROUTE_DISTANCE, distance);
    int rowsAffected = db.update(TABLE_ROUTES, contentValues, KEY_ROUTE_ID + "=" + mapId, null);

    return rowsAffected;
  }


  public boolean isOpen(){
    SQLiteDatabase db = this.getReadableDatabase();
    boolean isOpen = db.isOpen();
    return isOpen;
  }
}
