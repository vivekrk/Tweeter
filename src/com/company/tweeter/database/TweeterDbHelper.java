package com.company.tweeter.database;

import com.company.tweeter.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TweeterDbHelper extends SQLiteOpenHelper {

	private static final String CREATE_DATABASE = "CREATE TABLE tweets (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
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
		// TODO Auto-generated method stub

	}
}
