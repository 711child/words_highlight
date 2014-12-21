package com.example.highlight;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordDBHelper extends SQLiteOpenHelper {
	
	private static final String DB2_NAME = "words.db";
	private static final int version = 1; //Êý¾Ý¿â°æ±¾

	
	WordDBHelper(Context context) {
		super(context, DB2_NAME, null, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
					"words(word String PRIMARY KEY , level INTEGER)");
			
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL( "DROP TABLE IF EXISTS words");
	}

}
