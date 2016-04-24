package com.fly.perfectnotepad.base;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fly.perfectnotepad.R;
import com.fly.perfectnotepad.db.NoteDBHelper;


public class BaseApplication extends Application {

	private static BaseApplication instance;
	
	private  static boolean visibleFlag=false;
	
	

	public static boolean isVisibleFlag() {
		return visibleFlag;
	}

	public static void setVisibleFlag(boolean visibleFlag) {
		BaseApplication.visibleFlag = visibleFlag;
	}

	private static NoteDBHelper noteDBHelper;
	private static SQLiteDatabase  sqLiteDatabase;

	public static NoteDBHelper getNoteDBHelper() {
		noteDBHelper=new NoteDBHelper(getInstance(), "notes.db");
		return noteDBHelper;
	}

	public static SQLiteDatabase getSqLiteDatabase() {
		sqLiteDatabase=noteDBHelper.getWritableDatabase();
		return sqLiteDatabase;
	}

	

	public static BaseApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
	}

	public void showCustomToast(int resId) {
		View toastRootView = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.common_toast, null);
		((TextView) toastRootView.findViewById(R.id.toast_text))
				.setText(getString(resId));
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRootView);
		toast.show();
	}

	public void showCustomToast(String res) {
		View toastRootView = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.common_toast, null);
		((TextView) toastRootView.findViewById(R.id.toast_text))
		.setText(res);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRootView);
		toast.show();
	}
	
	
}
