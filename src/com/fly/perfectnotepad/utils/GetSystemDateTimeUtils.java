package com.fly.perfectnotepad.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class GetSystemDateTimeUtils {
	
	/**
	 * 这个方法：返回当前测试手机的日期和时间，按照固定的格式输出。
	 * @return 时间日期的字符串
	 */
	public static String getTime() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		Date date = new Date();
		String time = sdFormat.format(date);
		return time;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getTimeAsName() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd  HH-mm-ss");
		Date date = new Date();
		String time = sdFormat.format(date);
		return time;
	}

	
}
