package com.example.highlight;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;





public class LessonDBHelper extends SQLiteOpenHelper {
	
	private static final String DB1_NAME = "lesson.db"; //数据库名称
	//private static final String DB2_NAME = "words.db";
	private static final int version = 1; //数据库版本

	
	LessonDBHelper(Context context) {
		super(context, DB1_NAME, null, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " +
					"lesson(id INTEGER PRIMARY KEY , content VARCHAR)");
			
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL( "DROP TABLE IF EXISTS lesson");
	}

}