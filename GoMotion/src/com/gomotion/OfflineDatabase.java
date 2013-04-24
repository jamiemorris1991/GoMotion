package com.gomotion;

import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class OfflineDatabase extends SQLiteOpenHelper
{
	// All Static variables
    // Database Version
    private final static int DATABASE_VERSION = 1;
 
    // Database Name
    public final static String DATABASE_NAME = "offlineDB";
 
    // Table names
    public final static String TABLE_BODYWEIGHT = "bodyweight";
    public final static String TABLE_CARDIO = "cardio";
    public final static String TABLE_WAYPOINTS = "waypoints";
 
    // Exercise column names
    public final static String KEY_ID = "_id";
    public final static String KEY_TIMESTAMP = "timestamp";
    public final static String KEY_TYPE = "type";
    public final static String KEY_NAME = "name";

    // Body Weight Exercise Table Column names
    public final static String KEY_SETS = "sets";
    public final static String KEY_REPS = "reps";
    
    // Cardio Exercise Table Column names
    public final static String KEY_TIMELENGTH = "timelength";
    public final static String KEY_DISTANCE = "distance";

    // Waypoint Table Column names
    public final static String KEY_CARDIO_ID = "cid";
    public final static String KEY_LATITUDE = "latitude";
    public final static String KEY_LONGITUDE = "longitude";
    
    public OfflineDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// Create Body Weight Exercise table
		String CREATE_BODYWEIGHT_TABLE = "CREATE TABLE " + TABLE_BODYWEIGHT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIMESTAMP + " INTEGER,"
                + KEY_SETS + " INTEGER," + KEY_REPS + " INTEGER," +  KEY_TYPE + " TEXT," 
                + KEY_NAME + " TEXT" + ")";
		
        db.execSQL(CREATE_BODYWEIGHT_TABLE);
        
        // Create Cardio Exercise table
 		String CREATE_CARDIO_TABLE = "CREATE TABLE " + TABLE_CARDIO + "("
                 + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIMESTAMP + " INTEGER,"
                 + KEY_DISTANCE + " INTEGER," + KEY_TIMELENGTH + " INTEGER," +  KEY_TYPE + " TEXT" +")";
 		
         db.execSQL(CREATE_CARDIO_TABLE);
         
         String CREATE_WAYPOINTS_TABLE = "CREATE TABLE " + TABLE_WAYPOINTS + "("
        		 + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CARDIO_ID + " INTEGER," 
        		 + KEY_LATITUDE + " REAL," + KEY_LONGITUDE 
        		 + " REAL, FOREIGN KEY (" + KEY_CARDIO_ID + ") REFERENCES " + TABLE_CARDIO + "(" + KEY_ID + ")" + ")";
         
         db.execSQL(CREATE_WAYPOINTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		 // Drop older tables if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BODYWEIGHT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYPOINTS);

        // Create tables again
        onCreate(db);
	}
	
	/****************************** Body Weight exercise methods ***********************************************/
	
	// Add a new body weight exercise
	public boolean add(BodyWeightExercise exercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, exercise.getTimeStamp());
		values.put(KEY_SETS, exercise.getSets());
		values.put(KEY_REPS, exercise.getReps());
		values.put(KEY_TYPE, exercise.getType().name());
		values.put(KEY_NAME, exercise.getName());
		
		// Insert into database
		db.insert(TABLE_BODYWEIGHT, null, values);
		db.close();
		return true;
	}
	
	// Get a single body weight exercise
	public BodyWeightExercise getBodyWeightExercise(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_BODYWEIGHT, new String[] { KEY_ID, KEY_TIMESTAMP, 
				KEY_SETS, KEY_REPS, KEY_TYPE, KEY_NAME }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);

		if(cursor.moveToFirst()) 
		{		
			BodyWeightExercise exercise = new BodyWeightExercise(
					Integer.parseInt(cursor.getString(0)),
					Long.parseLong(cursor.getString(1)), 
					Integer.parseInt(cursor.getString(2)),
					Integer.parseInt(cursor.getString(3)),
					BodyWeightExercise.BodyWeightType.valueOf(cursor.getString(4)),
					cursor.getString(5));
			
			cursor.close();
			
			return exercise;
		}
		else return null;
	}
	
	// Returns a list of all body weight exercises
	public Cursor getAllBodyWeightExercises()
	{		
		String query = "SELECT * FROM " + TABLE_BODYWEIGHT;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor != null)
		{
			cursor.moveToFirst();
		} 
		
		db.close();
		
		return cursor;
	}
	
	// Get count of all body weight exercises
	public int getBodyWeightExercisesCount() 
	{
		String query = "SELECT * FROM " + TABLE_BODYWEIGHT;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		cursor.close();
		
		return cursor.getCount();
	}
	
	// Updates a single body weight exercise
	public int updateBodyWeightExercise(BodyWeightExercise exercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, exercise.getTimeStamp());
		values.put(KEY_SETS, exercise.getSets());
		values.put(KEY_REPS, exercise.getReps());
		values.put(KEY_TYPE, exercise.getType().toString());
		
		return db.update(TABLE_BODYWEIGHT, values, KEY_ID + " = ?", new String[] { String.valueOf(exercise.getID())});
	}
	
	// Delete a single body weight exercise
	public void deleteBodyWeightExercise(int id) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BODYWEIGHT, KEY_ID + " = ?", new String[] { String.valueOf(id)});
		db.close();
	}
	
	/****************************** Cardio exercise methods ***********************************************/
	
	// Add a single cardio exercise to the database
	public boolean add(CardioExercise exercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, exercise.getTimeStamp());
		values.put(KEY_DISTANCE, exercise.getDistance());
		values.put(KEY_TIMELENGTH, exercise.getTimeLength());
		values.put(KEY_TYPE, exercise.getType().name());
		
		// Insert into database
		db.insert(TABLE_CARDIO, null, values);
		db.close();
		return true;
	}
	
	// Add a single cardio exercise to the database and return row id
	public int addCardioExercise(CardioExercise exercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, exercise.getTimeStamp());
		values.put(KEY_DISTANCE, exercise.getDistance());
		values.put(KEY_TIMELENGTH, exercise.getTimeLength());
		values.put(KEY_TYPE, exercise.getType().name());
		
		// Insert into database
		int id = (int) db.insert(TABLE_CARDIO, null, values);
		db.close();
		return id;
	}
	
	// Get a single cardio exercise
	public CardioExercise getCardioExercise(int id) 
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CARDIO, new String[] { KEY_ID, KEY_TIMESTAMP, 
				KEY_DISTANCE, KEY_TIMELENGTH, KEY_TYPE }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);

		if(cursor.moveToFirst()) 
		{		
			CardioExercise exercise = new CardioExercise(
					Integer.parseInt(cursor.getString(0)),
					Long.parseLong(cursor.getString(1)),
					Integer.parseInt(cursor.getString(2)),
					Integer.parseInt(cursor.getString(3)),
					CardioExercise.CardioType.valueOf(cursor.getString(4)));
			
			cursor.close();
			
			return exercise;
		}
		else return null;
	}
	
	// Returns a list of all cardio exercises
	public Cursor getAllCardioExercises() 
	{		
		String query = "SELECT * FROM " + TABLE_CARDIO;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor != null)
		{
			cursor.moveToFirst();
		}
		
		db.close();
		
		return cursor;
	}
	
	// Get count of all cardio exercises
	public int getCardioExercisesCount() 
	{
		String query = "SELECT * FROM " + TABLE_CARDIO;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		cursor.close();
		
		return cursor.getCount();
	}
	
	// Updates a single cardio exercise
	public int updateCardioExercise(CardioExercise exercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, exercise.getTimeStamp());
		values.put(KEY_DISTANCE, exercise.getDistance());
		values.put(KEY_TIMELENGTH, exercise.getTimeLength());
		values.put(KEY_TYPE, exercise.getType().toString());
		
		return db.update(TABLE_CARDIO, values, KEY_ID + " = ?", new String[] { String.valueOf(exercise.getID())});
	}
	
	// Delete a single cardio exercise
	public void deleteCardioExercise(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CARDIO, KEY_ID + " = ?", new String[] { String.valueOf(id)});
		db.close();
	}
	
	/****************************** Waypoints table methods ***********************************************/
	
	public void addWaypoints(int cid, LinkedList<Location> waypoints)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		for(Location loc : waypoints)
		{
			ContentValues values = new ContentValues();
			values.put(KEY_CARDIO_ID, cid);
			values.put(KEY_LATITUDE, loc.getLatitude());
			values.put(KEY_LONGITUDE, loc.getLongitude());
			
			db.insert(TABLE_WAYPOINTS, null, values);
		}
		
		db.close();
	}
	
	public Cursor getWaypoints(int cid)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_WAYPOINTS, new String[]{ KEY_LATITUDE, KEY_LONGITUDE }, KEY_CARDIO_ID + " = ?", new String[]{ String.valueOf(cid)}, null, null, null);
		
		if(cursor != null)
		{
			cursor.moveToFirst();
		}
		
		return cursor;
	}
}
