package com.fly.perfectnotepad.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PictureUtils {
	
	
	/**
	 *  将图片压缩成缩略图。---。只是按照比例进行缩放--缩放到你指定的区域内。
	 * @param path
	 * @param contentHeight---
	 * @param contentWidth----
	 * @return 一个BitMap.
	 */
	public static  Bitmap getBitmap(String path,int contentHeight,int contentWidth) {

		// 先解析图片边框的大小
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeFile(path, ops);
		ops.inSampleSize = 1;
		int oHeight = ops.outHeight;
		int oWidth = ops.outWidth;

		// 控制压缩比
//		int contentHeight = 0;
//		int contentWidth = 0;
//		contentHeight = 200;
//		contentWidth = 200;
		if (((float) oHeight / contentHeight) < ((float) oWidth / contentWidth)) {
			ops.inSampleSize = (int) Math.ceil((float) oWidth / contentWidth);
		} 
		else {
			ops.inSampleSize = (int) Math.ceil((float) oHeight / contentHeight);
		}
		ops.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, ops);
		return bm;
	}
	
	
	
	/**
	 * 将BitMap对象保存到指定的路径。。然后返回那个路径
	 * @param bm
	 */
	public  static String saveBitmap(Bitmap bm,String picName) {
		String 	newFilePath="/storage/sdcard0/temp_image/"+picName+".jpg";
		File f = new File(newFilePath);
	        try {  
	            f.createNewFile();  
	        } catch (IOException e) {  
	            System.out.println("在保存图片时出错：" + e.toString());  
	        }  
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return newFilePath;
	}

}
