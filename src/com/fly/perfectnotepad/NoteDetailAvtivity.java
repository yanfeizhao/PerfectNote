package com.fly.perfectnotepad;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.fly.perfectnotepad.adapter.SpinnerAdapter;
import com.fly.perfectnotepad.adapter.SpinnerAdapter.onPhotoButtonClickListener;
import com.fly.perfectnotepad.base.BaseApplication;
import com.fly.perfectnotepad.db.NoteDBHelper;
import com.fly.perfectnotepad.entity.Note;
import com.fly.perfectnotepad.utils.DBOperatorUtils;
import com.fly.perfectnotepad.utils.GetSystemDateTimeUtils;
import com.fly.perfectnotepad.utils.PictureUtils;

@SuppressLint("NewApi")
public class NoteDetailAvtivity extends Activity implements OnClickListener,
		onPhotoButtonClickListener {
 
	private SQLiteDatabase mSqLiteDatabase = null;
	private NoteDBHelper mNoteDBHelper;

	private Button mKeepEdit;
	private Button mReturnButton;
	private Button mChangePhotoButton;
	private Button mStartVideoButton;
	private Button mShareButton;

	private EditText mTitleEditText;
	private EditText mAddressEditText;
	private EditText mContentEditText;
	private TextView mTypeTextView;
	private TextView mTimeTextView;

	private ImageView mPhotoImageView;
	private List<String> mChangephotoTypeList;
	public static PopupWindow mPopupWindowChangePhoto;
	private SpinnerAdapter mSpinnerAdapter;

	private ImageView mVideoImageView;

	private String timeParam;
	private List<Note> mList;

	private Note note;
	private Bitmap photo;
	private File mPhotoFile;
	private int CAMERA_RESULT = 100;
	private int RESULT_LOAD_IMAGE = 200;
	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath() + "/temp_image";
	// 获得sdCard路径。“/sdcard”或者“/mnt/sdcard”或者//
	// “/storage/sdcard0”，--但是这个方法会很通用。。\

	private String mPathOfPic;
	private boolean changePhotoFlag = false;
	
	
	private ImageView mPlayAnimaImageView;
	private  Button  mPlayVoiceButton;
	private boolean mStartPlaying = true;
	 private MediaPlayer   mPlayer = null;
	 private String mVoicePath = null;
	 private AnimationDrawable frameAnimationDrawable=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_notedetail);

		Intent intent = getIntent();
		intent.getStringExtra("time");
		timeParam = intent.getStringExtra("time");

		initData();
		initView();
		showItemDetial();

	}

	private void initData() {
		mNoteDBHelper = new NoteDBHelper(this, "notes.db");
		mSqLiteDatabase = mNoteDBHelper.getWritableDatabase();
		mList = new ArrayList<Note>();
		mList = DBOperatorUtils.queryNotesByTime(mSqLiteDatabase, timeParam);

		mChangephotoTypeList = new ArrayList<String>();
		String[] photoType = new String[] { "拍照", "图库", "取消" };
		for (int i = 0; i < photoType.length; i++) {
			mChangephotoTypeList.add(photoType[i]);
		}
	}

	@SuppressLint("NewApi")
	private void showItemDetial() {
		note = mList.get(0);
		mTitleEditText.setText( note.getTitle());
		mAddressEditText.setText( note.getAddress());
		mContentEditText.setText( note.getContent());
		mTypeTextView.setText(note.getType());
		mTimeTextView.setText(note.getTime());
		if (note.getPath()!=null) {
			Bitmap bm = PictureUtils.getBitmap(note.getPath(), 100,
					100);
			mPhotoImageView.setImageBitmap(bm);
		}
		if(note.getVideo()!=null){
			Bitmap bitmap=PictureUtils.getBitmap(note.getPathOfFirstVideo(), 100, 100);
			 mVideoImageView.setImageBitmap(bitmap);
		}
	}
	/**
	 * 初始化View
	 */
	private void initView() {
		mTitleEditText = (EditText) findViewById(R.id.et_title);
		mAddressEditText = (EditText) findViewById(R.id.et_address);
		mContentEditText = (EditText) findViewById(R.id.et_content);
		mTypeTextView = (TextView) findViewById(R.id.tv_type);
		mTimeTextView = (TextView) findViewById(R.id.tv_time);
		mChangePhotoButton = (Button) findViewById(R.id.btn_changePhoto);
		mPhotoImageView = (ImageView) findViewById(R.id.img_photo);
		mVideoImageView = (ImageView) findViewById(R.id.img_detail_video);
		mStartVideoButton=(Button) findViewById(R.id.btn_video);
		mPlayAnimaImageView=(ImageView) findViewById(R.id.img_detail_voice);
		mPlayVoiceButton=(Button) findViewById(R.id.btn_voice);
		mShareButton=(Button) findViewById(R.id.btn_share);
		
		//设置语音播放时候的动画效果
		frameAnimationDrawable=(AnimationDrawable) getResources().getDrawable(R.drawable.play_animation);
		mPlayAnimaImageView.setBackground(frameAnimationDrawable);

		mKeepEdit = (Button) findViewById(R.id.btn_keepedit);
		mReturnButton = (Button) findViewById(R.id.btn_return);
		mKeepEdit.setOnClickListener(this);
		mReturnButton.setOnClickListener(this);
		mChangePhotoButton.setOnClickListener(this);// 跟添加里的那个button一样，也会弹出下拉框。而且显示的内容是一样的。
		mStartVideoButton.setOnClickListener(this);// 点击之后会播放视频。。
		mPlayVoiceButton.setOnClickListener(this);//播放语音的
		mShareButton.setOnClickListener(this);//分享
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_keepedit:
			// 获取画面上的内容，并update到数据库--根据time来判断。
			getNoteDetailAndInsert();
			finish();
			break;
		case R.id.btn_return:
			finish();
			break;
		case R.id.btn_changePhoto:
			// 依旧会有个popwindow，用来显示你是以以何种方式来更换图片。图库，相机。然后选择。。
			showList(v);
			break;
		case R.id.btn_video:
			// 会播放视频。。
			if (note.getVideo()!=null) {
				startVideo();
			} else {
				BaseApplication.getInstance().showCustomToast("亲，您还没有录制视频哟！");
			}
			break;
		case R.id.btn_voice:
			//会播放或者停止语音。
			if (note.getVoice()==null) {
				BaseApplication.getInstance().showCustomToast("亲，您还没有录制语音哟！");
			} else {
				if(note.getVoice().equals("null")){
					BaseApplication.getInstance().showCustomToast("亲，您还没有录制语音哟！");
				}else{
					playVoice();
				}
			}
			break;
		case R.id.btn_share:
			Bundle bundle = new Bundle();
			bundle.putSerializable("note", mList.get(0));
			Intent intent =new Intent(this,ShareActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	
	
	
	private void playVoice() {
		if (mStartPlaying) {
			mPlayVoiceButton.setText("播放语音");
			frameAnimationDrawable.start();
		    mPlayer = new MediaPlayer();
		    try {
		    	mVoicePath=note.getVoice();
		        mPlayer.setDataSource(mVoicePath);//根据路径找到录音，并播放。。
		        mPlayer.prepare();
		        mPlayer.start();
		    } catch (IOException e) {
		        Log.e("提示：", "prepare() failed");
		    }
		
		} else {
			frameAnimationDrawable.stop();
			mPlayVoiceButton.setText("停止播放");
		    mPlayer.release();
		    mPlayer = null;
		}
		mStartPlaying = !mStartPlaying;
	}

	// 开始寻找手机上可以播放视频的软件，播放视频。
	private void startVideo() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
//		Uri uri = Uri.fromFile(new File("/storage/sdcard0/相机/录像/video_20160311_094351.3gp"));
		 Uri uri = Uri.fromFile(new File(note.getVideo()));
		intent.setDataAndType(uri, "video/*");
		startActivity(intent);
	}

	@SuppressWarnings("deprecation")
	private void showList(View v) {

		int id = v.getId();
		View view = View.inflate(this, R.layout.popupwindow_list, null); 
		int width = mChangePhotoButton.getWidth();
		mPopupWindowChangePhoto = new PopupWindow(view, width,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		// 点击任何位置，mPopupWindow要消失：[以下两个要搭配使用] -----在显示之前设置属性
		mPopupWindowChangePhoto.setBackgroundDrawable(new BitmapDrawable());// 缺少这个，下面的会失效
		mPopupWindowChangePhoto.setOutsideTouchable(true);

		initListView(view, id);

		// 显示在某个控件的下面，这里是btn_spinner下面
		switch (id) {
		case R.id.btn_changePhoto:
			mPopupWindowChangePhoto.showAsDropDown(mChangePhotoButton);
			break;

		default:
			break;
		}

	}

	private void initListView(View view, int id) {
		// TODO Auto-generated method stub
		ListView listView = (ListView) view.findViewById(R.id.lv_spinner);

		switch (id) {
		
		case R.id.btn_changePhoto:
			mSpinnerAdapter = new SpinnerAdapter(this, mChangephotoTypeList, id);
			listView.setAdapter(mSpinnerAdapter);
			mSpinnerAdapter.setOnPhotoButtonClickListener(this);
			break;

		default:
			break;
		}

	}

	/**
	 * 获取到界面上的已经修改了的数据，并实现数据库额更新。
	 */
	private void getNoteDetailAndInsert() {
		// TODO Auto-generated method stub
		String title = mTitleEditText.getText().toString();
		// String address = mAddressEditText.getText().toString();
		String content = mContentEditText.getText().toString();
		String type = mTypeTextView.getText().toString();
		String time = GetSystemDateTimeUtils.getTime();// ---本次修改的时间。。
		String time1 = mTimeTextView.getText().toString();// 查询这条记录的时候，用到的参数
		String video = note.getVideo();// 因为我设定的视频不允许重新拍摄，所以不会被修改。按照刚开始显示的时候来操作就好了。
		String pathOfVideo=note.getPathOfFirstVideo();
		String address=mAddressEditText.getText().toString();

		String path=null;
		if (changePhotoFlag) {
			path = mPathOfPic;
		} else {
			path = note.getPath();
		}
		Note note1 = new Note(title, content, type, time, path, video,pathOfVideo,address);
		DBOperatorUtils.updateNotesByTime(mSqLiteDatabase, note1, time1);// 更新。。..
	}

	@Override
	public void dealPhoteTypeButtonEvent(String type) {
		// TODO Auto-generated method stub
		if (type.equals("拍照")) {
			// changePhotoFlag=true;
			mPopupWindowChangePhoto.dismiss();
			BaseApplication.getInstance().showCustomToast("准备拍照");
			// 调用系统的相机，拍照，获得路径，存到数据库，显示照片。
			destoryImage();
			String state = Environment.getExternalStorageState();// 获取外部存储的状态
			if (state.equals(Environment.MEDIA_MOUNTED)) {// 如果外部存储的状态是不是“可读可写”的
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
			// changePhotoFlag=true;
			mPopupWindowChangePhoto.dismiss();
			BaseApplication.getInstance().showCustomToast("从图库选择");
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);// RESULT_LOAD_IMAGE
															// :200 请求码为200

		} else if (type.equals("取消")) {
			BaseApplication.getInstance().showCustomToast("点击了取消");
			mPopupWindowChangePhoto.dismiss();
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
				if (!(mPhotoFile.getAbsolutePath()).equals("")) {
					changePhotoFlag = true;
				}
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
			if (!mPathOfPic.equals("")) {
				changePhotoFlag = true;
			}
			Log.e("相册里的图片的路径：", mPathOfPic);

			cursor.close();
			mPhotoImageView
					.setImageBitmap(BitmapFactory.decodeFile(mPathOfPic));
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
