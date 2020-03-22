package com.jlrutilities.burbenrunner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RouteDatabaseHelper extends SQLiteOpenHelper {

  // TAG
  private static final String TAG = "DatabaseHelper";

  // Database Info
  private static final String DATABASE_NAME = "routesDatabase";
  private static final int DATABASE_VERSION = 1;

  // Table Names
  private static final String TABLE_ROUTES = "routes";
  private static final String TABLE_MARKERS = "markers";

  // Routes Table Columns
  private static final String KEY_ROUTE_ID = "id";
  private static final String KEY_ROUTE_NAME = "routeName";

  // Markers Table Columns
  private static final String KEY_MARKER_ID = "id";
  private static final String KEY_MARKER_ROUTE = "routeId";
  private static final String KEY_MARKER_ORDER = "sequencePosition";
  private static final String KEY_MARKER_LAT = "latitude";
  private static final String KEY_MARKER_LONG = "longitude";

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
    String CREATE_ROUTES_TABLE = "CREATE TABLE " + TABLE_ROUTES +
        "(" +
        KEY_ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Primary key
        KEY_ROUTE_NAME + " TEXT" + // Route name
        ")";

    String CREATE_MARKERS_TABLE = "CREATE TABLE " + TABLE_MARKERS +
        "(" +
        KEY_MARKER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        KEY_MARKER_ORDER + " INTEGER, " +
        KEY_MARKER_LAT + " DECIMAL(9,6), " +
        KEY_MARKER_LONG + " DECIMAL(9,6), " +
        KEY_MARKER_ROUTE + " INTEGER REFERENCES " + TABLE_ROUTES +
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
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
      onCreate(db);
    }
  }
  // Example setting value in db
  public boolean addNewTable(String item) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(KEY_ROUTE_NAME, item);

    // Log it
    Log.d(TAG, "addData: Adding " + item + " to " + TABLE_ROUTES);

    // Attempt to add it to DB and check
    long result = db.insert(TABLE_ROUTES, null, contentValues);

    // if inserted incorrectly it will return -1
    if (result == -1){
      return false;
    } else {
      return true;
    }
  }

  public Cursor getData(){
    SQLiteDatabase db = this.getWritableDatabase();
    String query = "SELECT * FROM " + TABLE_ROUTES;
    Cursor data = db.rawQuery(query, null);
    return data;
  }
}
