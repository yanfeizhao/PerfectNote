package com.fly.perfectnotepad.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fly.perfectnotepad.db.NoteDBHelper;
import com.fly.perfectnotepad.entity.Note;

public class DBOperatorUtils {

	/**
	 * 增加一条笔记内容到数据库
	 * 
	 * @param sqLiteDatabase
	 */
	public static void addContent(SQLiteDatabase sqLiteDatabase,
			ContentValues values, Note note) {

		values.put(NoteDBHelper.TITLE, note.getTitle());
		values.put(NoteDBHelper.CONTENT, note.getContent());
		values.put(NoteDBHelper.TIME, GetSystemDateTimeUtils.getTime());
		values.put(NoteDBHelper.TYPE, note.getType());
		values.put(NoteDBHelper.PATH, note.getPath());
		values.put(NoteDBHelper.VIDEO, note.getVideo());
		values.put(NoteDBHelper.PATHFIRSTVIDEO, note.getPathOfFirstVideo());
		values.put(NoteDBHelper.VOICE, note.getVoice());
		values.put(NoteDBHelper.ADDRESS, note.getAddress());//-------

		sqLiteDatabase.insert(NoteDBHelper.TABLE_NAME, null, values);
		values.clear();
	}

	/**
	 * 删除一条指定的记录根据时间 或者是别的条目。
	 * 
	 * @param sqLiteDatabase
	 */
	public static void deleteContent(SQLiteDatabase sqLiteDatabase, String time) {
		// 删除数据库里的东西
		sqLiteDatabase.delete("notes", "time = ?", new String[] { time });
		// sqLiteDatabase.close();
	}

	/**
	 * 更新数据库 的一条数据或者几条数据。---其实，我们的功能是实现批量删除。。
	 * 
	 * @param sqLiteDatabase
	 * @param values
	 */
	public static void updateNotes(SQLiteDatabase sqLiteDatabase,
			ContentValues values) {
		// 把id大于6的条目的内容都更改为我是更新的数据。id>6的会被修改"
		values.put(NoteDBHelper.CONTENT, "我是更新的数据。id>6的会被修改");
		sqLiteDatabase.update(NoteDBHelper.TABLE_NAME, values, "_id>?",
				new String[] { "6" });
		values.clear();
	}

	/**
	 * 通过时间来找到相应的条目，实现对应的更新。
	 * 
	 * @param sqLiteDatabase
	 * @param values
	 */
	public static void updateNotesByTime(SQLiteDatabase sqLiteDatabase,
			Note note, String time) {
		ContentValues values = new ContentValues();
		// 时间为time的条目修改下

		values.put(NoteDBHelper.TITLE, note.getTitle());
		values.put(NoteDBHelper.CONTENT, note.getContent());
		 values.put(NoteDBHelper.ADDRESS, note.getAddress());//-----------
		values.put(NoteDBHelper.TYPE, note.getType());
		values.put(NoteDBHelper.TIME, note.getTime());
		values.put(NoteDBHelper.PATH, note.getPath());
		values.put(NoteDBHelper.VIDEO, note.getVideo());
		values.put(NoteDBHelper.PATHFIRSTVIDEO, note.getPathOfFirstVideo());

		values.put(NoteDBHelper.VOICE, note.getVoice());

		sqLiteDatabase.update(NoteDBHelper.TABLE_NAME, values, "time = ? ",
				new String[] { time });
		values.clear();
	}

	/**
	 * 查询表里面所有的条目
	 * 
	 * @param sqLiteDatabase
	 * @return
	 */
	public static List<Note> queryNotes(SQLiteDatabase sqLiteDatabase) {
		List<Note> list = new ArrayList<Note>();
		Cursor mCursor = sqLiteDatabase.rawQuery("select * from notes ", null);
		if (mCursor != null) {
			mCursor.getColumnNames();
			while (mCursor.moveToNext()) {

				String title = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TITLE));
				String content = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.CONTENT));
				 String address=mCursor.getString(mCursor.getColumnIndex(NoteDBHelper.ADDRESS));//=============
				String type = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TYPE));
				String time = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TIME));
				String path = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.PATH));
				String video = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.VIDEO));
				String pathofvideo = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.PATHFIRSTVIDEO));

				String voice = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.VOICE));

//				Note note = new Note(title, content, type, time, path, video,
//						pathofvideo, voice);
				Note note = new Note(title, content, type, time, path, video,
						pathofvideo, voice,address);
				list.add(note);
			}
			mCursor.close();
		}

		return list;
	}

	/**
	 * 通过时间找到数据库里对应的条目。
	 * 
	 * @param sqLiteDatabase
	 * @return
	 */
	public static List<Note> queryNotesByTime(SQLiteDatabase sqLiteDatabase,
			String time) {
		List<Note> list = new ArrayList<Note>();
		Note note;
		Cursor mCursor = sqLiteDatabase.rawQuery(
				"select * from notes where time = '" + time + "'", null);// time='49'
																			// 别忘了单引号
		if (mCursor != null) {
			while (mCursor.moveToNext()) {
				String title = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TITLE));
				String content = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.CONTENT));
				 String  address=mCursor.getString(mCursor.getColumnIndex(NoteDBHelper.ADDRESS));//=====
				String type = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TYPE));
				// String time1 =
				// mCursor.getString(mCursor.getColumnIndex(NoteDBHelper.TIME));
				String path = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.PATH));
				String video = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.VIDEO));
				String pathofvideo = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.PATHFIRSTVIDEO));
				String voice = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.VOICE));
//
//				note = new Note(title, content, type, time, path, video,
//						pathofvideo, voice);
				note = new Note(title, content, type, time, path, video,
						pathofvideo, voice,address);
				list.add(note);
			}
			mCursor.close();
		}
		return list;
	}

	/**
	 * 根据笔记的type和包含何种类型的文件进行查询。
	 * 
	 * @return
	 */
	public static List<Note> queryNoteByType(SQLiteDatabase sqLiteDatabase,
			String notetype, String fileType) {
		List<Note> list = new ArrayList<Note>();
		Note note;
		Cursor mCursor = null;
		
		String sql="";
		if (notetype.equals("笔记类别")||notetype.equals("全部类型")) {
			if (fileType.equals("媒体类型") || fileType.equals("所有媒体")) {
				sql="select * from notes ";
			} else if (fileType.equals("语音")) {
				sql="select * from notes where voice not null";
			} else if (fileType.equals("视频")) {
				sql="select * from notes where video not null";
			} else if (fileType.equals("图片")) {
				sql="select * from notes where path not null";
			}
		} 
		else {
			if (fileType.equals("媒体类型") || fileType.equals("所有媒体")) {
				sql="select * from notes where type = '"+notetype+"' ";
			} else if (fileType.equals("语音")) {
				sql="select * from notes where type = '"+notetype+"' and voice not null ";
			} else if (fileType.equals("视频")) {
				sql="select * from notes where type = '"+notetype+"' and video not null ";
			} else if (fileType.equals("图片")) {
				sql="select * from notes where type = '"+notetype+"' and path not null ";
			}
		}
		
		mCursor = sqLiteDatabase.rawQuery(sql, null);
		if (mCursor != null) {
			while (mCursor.moveToNext()) {
				String title = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TITLE));
				String content = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.CONTENT));
				 String
				 address=mCursor.getString(mCursor.getColumnIndex(NoteDBHelper.ADDRESS));
				String type = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TYPE));
				String time = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.TIME));
				String path = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.PATH));
				String video = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.VIDEO));
				String pathofvideo = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.PATHFIRSTVIDEO));
				String voice = mCursor.getString(mCursor
						.getColumnIndex(NoteDBHelper.VOICE));

//				note = new Note(title, content, type, time, path, video,
//						pathofvideo, voice);
				note = new Note(title, content, type, time, path, video,
						pathofvideo, voice,address);
				list.add(note);
			}
			mCursor.close();
		}
		return list;
	}

}
