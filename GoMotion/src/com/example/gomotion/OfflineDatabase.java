package com.example.gomotion;

import java.util.ArrayList;
import java.util.List;

import com.example.gomotion.BodyWeightExcercise.BodyweightType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OfflineDatabase extends SQLiteOpenHelper implements Database
{
	// All Static variables
    // Database Version
    private final int DATABASE_VERSION = 1;
 
    // Database Name
    protected final String DATABASE_NAME = "offlineDB";
 
    // Table names
    private final String TABLE_BODYWEIGHT = "bodyweight";
    private final String TABLE_CARDIO = "cardio";
 
    // Duplicate column names
    private final String KEY_ID = "_id";
    private final String KEY_TIMESTAMP = "timestamp";
    private final String KEY_TYPE = "type";

    // Body Weight Excercise Table Column names
    private final String KEY_SETS = "sets";
    private final String KEY_REPS = "reps";
    
    // Cardio Excercise Table Column names
    private final String KEY_TIMELENGTH = "timelength";
    private final String KEY_DISTANCE = "distance";

    public OfflineDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// Create Body Weight Excercise table
		String CREATE_BODYWEIGHT_TABLE = "CREATE TABLE " + TABLE_BODYWEIGHT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIMESTAMP + " INTEGER,"
                + KEY_SETS + " INTEGER," + KEY_REPS + " INTEGER," +  KEY_TYPE + " TEXT" +")";
		
        db.execSQL(CREATE_BODYWEIGHT_TABLE);
        
        // Create Cardio Excercise table
 		String CREATE_CARDIO_TABLE = "CREATE TABLE " + TABLE_CARDIO + "("
                 + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIMESTAMP + " INTEGER,"
                 + KEY_DISTANCE + " INTEGER," + KEY_TIMELENGTH + " INTEGER," +  KEY_TYPE + " TEXT" +")";
 		
         db.execSQL(CREATE_CARDIO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		 // Drop older tables if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BODYWEIGHT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDIO);

        // Create tables again
        onCreate(db);
	}
	
	/****************************** Body Weight excercise methods ***********************************************/
	
	// Add a new body weight excercise
	public boolean add(BodyWeightExcercise excercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, excercise.getTimeStamp());
		values.put(KEY_SETS, excercise.getSets());
		values.put(KEY_REPS, excercise.getReps());
		values.put(KEY_TYPE, excercise.getType().name());
		
		// Insert into database
		db.insert(TABLE_BODYWEIGHT, null, values);
		db.close();
		return true;
	}
	
	// Get a single body weight excercise
	public BodyWeightExcercise getBodyWeightExcercise(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_BODYWEIGHT, new String[] { KEY_ID, KEY_TIMESTAMP, 
				KEY_SETS, KEY_REPS, KEY_TYPE }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);

		if(cursor.moveToFirst()) 
		{		
			BodyWeightExcercise excercise = new BodyWeightExcercise(
					Integer.parseInt(cursor.getString(0)),
					Integer.parseInt(cursor.getString(1)), 
					Integer.parseInt(cursor.getString(2)),
					Integer.parseInt(cursor.getString(3)),
					BodyWeightExcercise.BodyweightType.valueOf(cursor.getString(4)));
			
			cursor.close();
			
			return excercise;
		}
		else return null;
	}
	
	// Returns a list of all body weight excercises
	public List<BodyWeightExcercise> getAllBodyWeightExcercises()
	{
		List<BodyWeightExcercise> excerciseList = new ArrayList<BodyWeightExcercise>();
		
		String query = "SELECT * FROM " + TABLE_BODYWEIGHT;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			do {
				BodyWeightExcercise excercise = new BodyWeightExcercise(
						Integer.parseInt(cursor.getString(0)),
						Integer.parseInt(cursor.getString(1)),
						Integer.parseInt(cursor.getString(2)),
						Integer.parseInt(cursor.getString(3)),
						BodyWeightExcercise.BodyweightType.valueOf(cursor.getString(4))	
				);
				excerciseList.add(excercise);
			} while(cursor.moveToNext());
		}
		
		return excerciseList;
	}
	
	// Get count of all body weight excercises
	public int getBodyWeightExcercisesCount() 
	{
		String query = "SELECT * FROM " + TABLE_BODYWEIGHT;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		cursor.close();
		
		return cursor.getCount();
	}
	
	// Updates a single body weight excercise
	public int updateBodyWeightExcercise(BodyWeightExcercise excercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, excercise.getTimeStamp());
		values.put(KEY_SETS, excercise.getSets());
		values.put(KEY_REPS, excercise.getReps());
		values.put(KEY_TYPE, excercise.getType().toString());
		
		return db.update(TABLE_BODYWEIGHT, values, KEY_ID + " = ?", new String[] { String.valueOf(excercise.getID())});
	}
	
	// Delete a single body weight excercise
	public void deleteBodyWeightExcercise(BodyWeightExcercise excercise) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BODYWEIGHT, KEY_ID + " = ?", new String[] { String.valueOf(excercise.getID())});
		db.close();
	}
	
	/****************************** Cardio excercise methods ***********************************************/
	
	// Add a single cardio excercise to the database
	public boolean add(CardioExcercise excercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, excercise.getTimeStamp());
		values.put(KEY_DISTANCE, excercise.getDistance());
		values.put(KEY_TIMELENGTH, excercise.getTimeLength());
		values.put(KEY_TYPE, excercise.getType().name());
		
		// Insert into database
		db.insert(TABLE_CARDIO, null, values);
		db.close();
		return true;
	}
	
	// Get a single cardio excercise
	public CardioExcercise getCardioExcercise(int id) 
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CARDIO, new String[] { KEY_ID, KEY_TIMESTAMP, 
				KEY_DISTANCE, KEY_TIMELENGTH, KEY_TYPE }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);

		if(cursor.moveToFirst()) 
		{		
			CardioExcercise excercise = new CardioExcercise(
					Integer.parseInt(cursor.getString(0)),
					Integer.parseInt(cursor.getString(1)),
					Integer.parseInt(cursor.getString(2)),
					Integer.parseInt(cursor.getString(3)), 
					CardioExcercise.CardioType.valueOf(cursor.getString(4)));
			
			cursor.close();
			
			return excercise;
		}
		else return null;
	}
	
	// Returns a list of all cardio excercises
	public List<CardioExcercise> getAllCardioExcercises() 
	{
		List<CardioExcercise> excerciseList = new ArrayList<CardioExcercise>();
		
		String query = "SELECT * FROM " + TABLE_CARDIO;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			do {
				CardioExcercise excercise = new CardioExcercise();
				
				excercise.setID(Integer.parseInt(cursor.getString(0)));
				excercise.setTimeStamp(Integer.parseInt(cursor.getString(1)));
				excercise.setDistance(Integer.parseInt(cursor.getString(2)));
				excercise.setTimeLength(Integer.parseInt(cursor.getString(3)));
				excercise.setType(CardioExcercise.CardioType.valueOf(cursor.getString(4)));	
				
				excerciseList.add(excercise);
			} while(cursor.moveToNext());
		}
		
		return excerciseList;
	}
	
	// Get count of all cardio excercises
	public int getCardioExcercisesCount() 
	{
		String query = "SELECT * FROM " + TABLE_CARDIO;		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		cursor.close();
		
		return cursor.getCount();
	}
	
	// Updates a single cardio excercise
	public int updateCardioExcercise(CardioExcercise excercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, excercise.getTimeStamp());
		values.put(KEY_DISTANCE, excercise.getDistance());
		values.put(KEY_TIMELENGTH, excercise.getTimeLength());
		values.put(KEY_TYPE, excercise.getType().toString());
		
		return db.update(TABLE_CARDIO, values, KEY_ID + " = ?", new String[] { String.valueOf(excercise.getID())});
	}
	
	// Delete a single cardio excercise
	public void deleteCardioExcercise(CardioExcercise excercise)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CARDIO, KEY_ID + " = ?", new String[] { String.valueOf(excercise.getID())});
		db.close();
	}
}
