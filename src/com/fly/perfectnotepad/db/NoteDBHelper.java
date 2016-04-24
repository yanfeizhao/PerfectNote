package com.fly.perfectnotepad.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "notes";// 表名字
	public static final String TITLE = "title";// ------not null
	public static final String CONTENT = "content";// ----not null
	public static final String ID = "_id";
	public static final String PATH = "path";// 存储图片--------
	public static final String VIDEO = "video";// 存储视频。------
	public static final String TIME = "time";// 记事的时间。----not null
	public static final String VOICE = "voice";// 记录的语音
	public static final String ADDRESS = "address";// 地点
	public static final String TYPE = "type";// 记事本的类型。-----not null

	public static final String PATHFIRSTVIDEO = "pathOfFirstVideo";

	public NoteDBHelper(Context context, String name) {
		super(context, name, null, 1);
		// TODO Auto-generated constructor stub
	}

	public NoteDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

//		db.execSQL("create table " + TABLE_NAME + " (" + ID
//				+ " integer primary key autoincrement , " + TITLE
//				+ "  TEXT NOT NULL , " + CONTENT + "  TEXT NOT NULL , " + TIME
//				+ "  TEXT NOT NULL , " + TYPE + "  TEXT NOT NULL , " + PATH
//				+ "   TEXT  , " + PATHFIRSTVIDEO + "   TEXT  , " + VOICE
//				+ "   TEXT  , " + VIDEO + "  TEXT)");
		
		db.execSQL("create table " + TABLE_NAME + " (" + ID
				+ " integer primary key autoincrement , " + TITLE
				+ "  TEXT NOT NULL , " + CONTENT + "  TEXT NOT NULL , " + TIME
				+ "  TEXT NOT NULL , " + TYPE + "  TEXT NOT NULL , " + PATH
				+ "   TEXT  , " + PATHFIRSTVIDEO + "   TEXT  , " + VOICE
				+ "   TEXT  , "  + ADDRESS
				+ "   TEXT  , " + VIDEO + "  TEXT)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
