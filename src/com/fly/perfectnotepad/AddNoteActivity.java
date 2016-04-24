package com.fly.perfectnotepad;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.provider.MediaStore.Video.VideoColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.fly.perfectnotepad.adapter.SpinnerAdapter;
import com.fly.perfectnotepad.adapter.SpinnerAdapter.onPhotoButtonClickListener;
import com.fly.perfectnotepad.base.BaseApplication;
import com.fly.perfectnotepad.db.NoteDBHelper;
import com.fly.perfectnotepad.entity.Note;
import com.fly.perfectnotepad.utils.DBOperatorUtils;
import com.fly.perfectnotepad.utils.GetSystemDateTimeUtils;
import com.fly.perfectnotepad.utils.PictureUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AddNoteActivity extends Activity implements OnClickListener,
		onPhotoButtonClickListener {
	
	public LocationClient mLocationClient ;
	public BDLocationListener myListener ;
	public static String address;
	public boolean locationFinishFlag=false;

	private EditText mTitleTextView;
	private EditText mAddressTextView;
	private EditText mContentTextView;
	public static TextView mTypeTextView;
	private TextView mTimeTextView;

	private Button mSureButton;
	private Button mCancelButton;
	public static Button mTakePhotoButton;
	private Button mVideoButton;
	private Button mLocationButton;

	private ContentValues mValues;
	private NoteDBHelper noteDBHelper;
	private SQLiteDatabase sqLiteDatabase;

	public static PopupWindow mPopupWindow;
	private List<String> mTakephotoTypeList;
	private List<String> mNoteTypeList;
	
	private SpinnerAdapter mSpinnerAdapter;
	private ImageView mPhotoImageView;//用来显示：拍的照片，或者是图，或者是图库里的图。
	private ImageView mVideoImageView;// 用来显示录制视频的第一帧。

	private String mPathOfPic=null;
	private String mVideoPath;
	private String mPathOfFirstVideo;

	private static Bitmap bitmapOfVideo;// 存放视频第一帧的bitmap
	private Bitmap photo;// 图片的

	private File mPhotoFile;
	private int CAMERA_RESULT = 100;
	private int RESULT_LOAD_IMAGE = 200;
	private static final int VIDEO_CAPTURE = 300;
	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath() + "/temp_image";// 获得sdCard路径。“/sdcard”或者“/mnt/sdcard”或者“/storage/sdcard0”，--但是这个方法会很通用。。
										
	private String mVoicePath = null;
	private Button mRecordButton = null;
	private MediaRecorder mRecorder = null;
	private ImageView mVoiceImageView;
	private AnimationDrawable frameAnimationDrawable = null;
	private boolean mStartRecording = true;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_addnote);
		initData();
		initView();
		File savePath = new File(saveDir);
		if (!savePath.exists()) {
			savePath.mkdirs();
		}
	}


	private void initData() {
		
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mValues = new ContentValues();
		
		noteDBHelper = new NoteDBHelper(this, "notes.db");
		sqLiteDatabase = noteDBHelper.getWritableDatabase();
		mValues = new ContentValues();

		mTakephotoTypeList = new ArrayList<String>();
		String[] photoType = new String[] { "拍照", "图库", "取消" };
		for (int i = 0; i < photoType.length; i++) {
			mTakephotoTypeList.add(photoType[i]);
		}
		
		mNoteTypeList = new ArrayList<String>();
		String[] noteType = new String[] { "工作类", "学习类", "生活类","情感类" ,"其他"};
		for (int i = 0; i < noteType.length; i++) {
			mNoteTypeList.add(noteType[i]);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		mTitleTextView = (EditText) findViewById(R.id.tv_title);
		mAddressTextView = (EditText) findViewById(R.id.tv_address);
		mContentTextView = (EditText) findViewById(R.id.tv_content);
		mTypeTextView = (TextView) findViewById(R.id.tv_type);
		mTimeTextView = (TextView) findViewById(R.id.tv_time);
		mPhotoImageView = (ImageView) findViewById(R.id.img_photo);
		mVideoImageView = (ImageView) findViewById(R.id.img_video);
		mLocationButton=(Button) findViewById(R.id.btn_location);
		
		mTimeTextView.setText(GetSystemDateTimeUtils.getTime().toString());
		mSureButton = (Button) findViewById(R.id.btn_sure);
		mCancelButton = (Button) findViewById(R.id.btn_cancel);

		mTakePhotoButton = (Button) findViewById(R.id.btn_takePhoto);
		mTakePhotoButton.setOnClickListener(this);

		mVideoButton = (Button) findViewById(R.id.btn_video);
		mVideoButton.setOnClickListener(this);
		// -----¼���йصġ���
		mRecordButton = (Button) findViewById(R.id.btn_voice);// ------它有自己的监听事件
		mVoiceImageView = (ImageView) findViewById(R.id.img_voice);
		frameAnimationDrawable = (AnimationDrawable) getResources()
				.getDrawable(R.drawable.record_animation);
		mVoiceImageView.setBackground(frameAnimationDrawable);
		mRecordButton.setOnClickListener(this);

		mSureButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		mTypeTextView.setOnClickListener(this);
		mLocationButton.setOnClickListener(this);
		

	}

	
	/**
	 *  开始录音  停止录音---
	 * @param start
	 */
	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		FileDescriptor mFileName;

		mVoicePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		mVoicePath += "/" + GetSystemDateTimeUtils.getTimeAsName() + ".3gp";

		mRecorder.setOutputFile(mVoicePath);// ----------------录音的存储路径
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("info", "prepare() failed");
		}
		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:
			// 获得文本框里的数据，并插入到数据库。
			getNoteAndInsert();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_takePhoto:
			showList(v);
			break;
		case R.id.btn_video:
			invokeVideo();// 调系统录像，开始录像。
			break;

		case R.id.btn_voice:
			
			onRecord(mStartRecording);
			if (mStartRecording) {
				frameAnimationDrawable.start();
				mRecordButton.setText("停止录音");
			} else {
				frameAnimationDrawable.stop();
				mRecordButton.setText("开始录音");
			}
			mStartRecording = !mStartRecording;
			break;
			
		case R.id.tv_type:
			showList(v);
			break;
		case R.id.btn_location:
			location();
			break;
			
		default:
			break;
		}
	}

	/**
	 * 开始定位。
	 */
	private void location() {

		
		// 设置定位参数
		LocationClientOption option = new LocationClientOption();
		// 设置成高精度定位
		option.setLocationMode(LocationMode.Hight_Accuracy);
		// 设置需要返回地址
		option.setIsNeedAddress(true);
		// 重复定位时间间隔
		option.setScanSpan(10000);
		mLocationClient.setLocOption(option);
		// 设置监听器
		mLocationClient
				.registerLocationListener(new BDLocationListener() {

					@Override
					public void onReceiveLocation(BDLocation location) {
						// 获取百度地图返回的信息后调用
						// 判断是否返回成功
						if (location == null) {
							return;
						}
						if (!location.getAddrStr().equals("")
								&& location.getAddrStr() != null) {
							// 成功获取到数据，关闭定位
							mAddressTextView.setText(location.getAddrStr());
							mLocationClient.stop();
						}
					}
				});
		mLocationClient.start();
		
	
	}

	/**
	 * 调用系统录像，进行录像，并记录视频的存储地址。
	 */
	private void invokeVideo() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);// extra_video_quality
		startActivityForResult(intent, VIDEO_CAPTURE);
	}

	@SuppressWarnings("deprecation")
	private void showList(View v) {

		int id = v.getId();
		View view = View.inflate(this, R.layout.popupwindow_list, null);
		int widthPhotoButton = mTakePhotoButton.getWidth();
		int widthType=mTypeTextView.getWidth();
		int heightType=mTypeTextView.getHeight();
		
		if(id==R.id.btn_takePhoto){
			mPopupWindow = new PopupWindow(view, widthPhotoButton,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		else if(id==R.id.tv_type){
			mPopupWindow = new PopupWindow(view, widthType,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		

		// 点击任何位置，mPopupWindow要消失：[以下两个要搭配使用] -----在显示之前设置属性
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 缺少这个，下面的会失效。
		mPopupWindow.setOutsideTouchable(true);

		initListView(view, id);

		// 显示在某个控件的下面，这里是btn_spinner下面
		switch (id) {
		case R.id.btn_takePhoto:
			mPopupWindow.showAsDropDown(mTakePhotoButton);
			break;
		case R.id.tv_type:
//			mPopupWindow.showAsDropDown(mTypeTextView);
			//TODO  
			mPopupWindow.showAsDropDown(mTypeTextView,0,-(9*heightType)+20);
			break;
		default:
			break;
		}
	}

	private void initListView(View view, int id) {
		// TODO Auto-generated method stub
		ListView listView = (ListView) view.findViewById(R.id.lv_spinner);

		switch (id) {
		case R.id.btn_takePhoto:
			mSpinnerAdapter = new SpinnerAdapter(this, mTakephotoTypeList, id);
			listView.setAdapter(mSpinnerAdapter);
			mSpinnerAdapter.setOnPhotoButtonClickListener(this);
			break;
		case R.id.tv_type:
			mSpinnerAdapter = new SpinnerAdapter(this, mNoteTypeList, id);
			listView.setAdapter(mSpinnerAdapter);
			mSpinnerAdapter.setOnPhotoButtonClickListener(this);
			break;
		default:
			break;
		}

	}

	private void getNoteAndInsert() {
		// TODO 将类型插入数据库。
		String mTitle = mTitleTextView.getText().toString();
		String mAddress = mAddressTextView.getText().toString();
		String mContent = mContentTextView.getText().toString();
		String mType = mTypeTextView.getText().toString();
		String mTime = mTimeTextView.getText().toString();

		Note note = new Note(mTitle, mContent, mType, mTime, mPathOfPic,
				mVideoPath, mPathOfFirstVideo, mVoicePath,mAddress);
		DBOperatorUtils.addContent(sqLiteDatabase, mValues, note);

		finish();
	}

	@Override
	public void dealPhoteTypeButtonEvent(String type) {
		// TODO Auto-generated method stub
		if (type.equals("拍照")) {
			mTakePhotoButton.setText("拍照");
			mPopupWindow.dismiss();
			BaseApplication.getInstance().showCustomToast("准备拍照");
			// 调用系统的相机，拍照，获得路径，存到数据库，显示照片。
			destoryImage();
			String state = Environment.getExternalStorageState();// 获取外部存储的状态״̬
			if (state.equals(Environment.MEDIA_MOUNTED)) {//如果外部存储的状态是不是“可读可写”的
				String picName = GetSystemDateTimeUtils.getTimeAsName();// 使用时间作为图片的名字。
				mPhotoFile = new File(saveDir, picName + ".jpg");// 文件名temp.jpg存在saveDir目录下
				mPhotoFile.delete();
				if (!mPhotoFile.exists()) {
					try {
						mPhotoFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						BaseApplication.getInstance().showCustomToast(
								" 照片创建失败!");
						return;
					}
				}
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(mPhotoFile));
				startActivityForResult(intent, CAMERA_RESULT);// 请求码为100
			} else {
				BaseApplication.getInstance()
						.showCustomToast(" sdcard无效或没有插入!");
			}
		} else if (type.equals("图库")) {
			mTakePhotoButton.setText("图库");
			mPopupWindow.dismiss();
			BaseApplication.getInstance().showCustomToast("从图库选择");
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);// RESULT_LOAD_IMAGE
															// :200 请求码为200

		} else if (type.equals("取消")) {
			mTakePhotoButton.setText("发照片");
			BaseApplication.getInstance().showCustomToast("点击了取消");
			mPopupWindow.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 从手机拍摄。。
		if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
			if (mPhotoFile != null && mPhotoFile.exists()) {

				mPathOfPic = mPhotoFile.getAbsolutePath();
				Log.e("文件绝对路径", mPhotoFile.getAbsolutePath());
				Bitmap bitmap = PictureUtils.getBitmap(mPathOfPic, 100, 100);
				mPhotoImageView.setImageBitmap(bitmap);
			}
		}
		// 从图库获取。。。
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			mPathOfPic = cursor.getString(columnIndex);
			Log.e("相册里的图片的路径：", mPathOfPic);
			cursor.close();
			mPhotoImageView
					.setImageBitmap(BitmapFactory.decodeFile(mPathOfPic));
		}
		// 获得系统录像的内容，以及路径，将第一帧显示在ImageView里面。
		if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURE
				&& null != data) {
			Uri uri = data.getData();
			Cursor cursor = this.getContentResolver().query(uri, null, null,
					null, null);
			if (cursor != null && cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex(VideoColumns._ID));
				mVideoPath = cursor.getString(cursor
						.getColumnIndex(VideoColumns.DATA));
				Toast.makeText(this, mVideoPath, Toast.LENGTH_LONG).show();
				Log.e("录像的名字", mVideoPath + "");
				bitmapOfVideo = Thumbnails.getThumbnail(getContentResolver(),
						id, Thumbnails.MICRO_KIND, null);
				// ------------------3-11
				// 第一帧的图片的名字是：在时间前面加上个a开始的
				mPathOfFirstVideo = PictureUtils.saveBitmap(bitmapOfVideo, "a"
						+ GetSystemDateTimeUtils.getTimeAsName());

				mVideoImageView.setImageBitmap(bitmapOfVideo);
				cursor.close();
			}
		}
	}

	@Override
	protected void onDestroy() {
		destoryImage();
		super.onDestroy();
	}

	private void destoryImage() {
		if (photo != null) {
			photo.recycle();// 解除本地绑定。
			photo = null;
		}
	}


}
