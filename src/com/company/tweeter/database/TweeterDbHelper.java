package com.company.tweeter.database;

import java.util.Date;

import twitter4j.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.company.tweeter.Constants;

public class TweeterDbHelper extends SQLiteOpenHelper {

//	private static final String TABLE_NAME = "twitterdata";
	private static final String CREATE_DATABASE = "CREATE TABLE twitterdata (_id TEXT PRIMARY KEY, "
			+ " time TEXT, username TEXT, image TEXT, tweet TEXT, retweetedby TEXT, timeline INTEGER);";
	
	public TweeterDbHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null, Constants.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS twitterdata");
		onCreate(db);
	}
	
	public void eraseDb(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS twitterdata");
	}
	
	public Cursor query(String tableName, String[] projection, String selection) {
		SQLiteDatabase db = getReadableDatabase();
		String orderBy = Constants.STATUS_ID + " DESC";
		Cursor data = db.query(tableName, projection, selection, null, null, null, orderBy);
		return data;
	}
	
	public void addStatus(Status status, int timelineType) {
		long statusID = status.getId();
		
		Date createdDate = status.getCreatedAt();
		String username = status.getUser().getScreenName();
		String imageUrl = status.getUser().getProfileImageURL().toString();
		String tweet = status.getText();
		String reTweetedBy = null;
		
		if(status.isRetweet()) {
			Log.d(Constants.TAG, status.getText());
			Status retweetedStatus = status.getRetweetedStatus();
			username = retweetedStatus.getUser().getScreenName();
			imageUrl = retweetedStatus.getUser().getProfileImageURL().toString();
			tweet = retweetedStatus.getText();
			reTweetedBy = status.getUser().getScreenName();
		}
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(Constants.STATUS_ID, statusID);
		cv.put(Constants.CREATED_TIME, createdDate.toString());
		cv.put(Constants.USERNAME, username);
		cv.put(Constants.PROFILE_IMAGE, imageUrl);
		cv.put(Constants.TWEET, tweet);
		cv.put(Constants.RETWEETED_BY, reTweetedBy);
		cv.put(Constants.TIMELINE, timelineType);
		
		try {
			db.insertWithOnConflict(Constants.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
		} catch (SQLException e) {
			e.getStackTrace();
		}
	}
}
