package com.company.tweeter.database;

import java.util.Date;

import twitter4j.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.company.tweeter.Constants;

public class TweeterDbHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "timeline";
	private static final String CREATE_DATABASE = "CREATE TABLE timeline (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ " time TEXT, username TEXT, image TEXT, tweet TEXT);";
	
	public TweeterDbHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null, Constants.DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_DATABASE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS tasks");
		onCreate(db);
	}
	
	public Cursor query(String tableName, String[] projection, String selection) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor data = db.query(tableName, projection, selection, null, null, null, null);
		return data;
	}
	
	public void addStatus(Status status) {
		Date createdDate = status.getCreatedAt();
		String username = status.getUser().getName();
		String imageUrl = status.getUser().getProfileImageURL().toString();
		String tweet = status.getText();
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(Constants.CREATED_TIME, createdDate.toString());
		cv.put(Constants.USERNAME, username);
		cv.put(Constants.PROFILE_IMAGE, imageUrl);
		cv.put(Constants.TWEET, tweet);
		
		db.insert(TABLE_NAME, Constants.TWEET, cv);
	}
}