package com.fly.perfectnotepad;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.fly.perfectnotepad.entity.Note;
import com.fly.perfectnotepad.utils.CheckAppExistUtils;
import com.fly.perfectnotepad.utils.PictureUtils;

public class ShareActivity extends Activity implements OnClickListener {
	private String mPackageQQ = "com.tencent.mobileqq";
	private String mPackageWeibo = "com.sina.weibo";
	private String mPackageWeixin = "com.tencent.mm";
	private String mPackageQQZone = "com.qzone";
	private String shareFail;
	private String shareCancel;
	private Note note ;
	
	private Button mButtonBack;
	private ImageView mImageViewWeiChat;
	private ImageView mImageViewWeiChatMomments;
	private ImageView mImageViewWeiBo;
	private ImageView mImageViewQQ;
	private ImageView mImageViewQZone;

	private TextView mTextViewNoteContent;
	private ImageView mImageViewNotePath;
	

	private LinearLayout mLayoutWeChat;
	private LinearLayout mLayoutFriends;
	private LinearLayout mLayoutWeibo;
	private LinearLayout mLayoutQQ;
	private LinearLayout mLayoutQQZone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_share);

		ShareSDK.initSDK(this);

		initData();
		initView();

	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		 note = (Note) bundle.getSerializable("note");// note里存储了刚才显示的笔记的全部内容。
	}

	private void initView() {
		mButtonBack = (Button) findViewById(R.id.btn_back_share);

		mImageViewWeiChat = (ImageView) findViewById(R.id.img_wechat);
		mImageViewWeiChatMomments = (ImageView) findViewById(R.id.img_friends);
		mImageViewWeiBo = (ImageView) findViewById(R.id.img_weibo);
		mImageViewQQ = (ImageView) findViewById(R.id.img_qq);
		mImageViewQZone = (ImageView) findViewById(R.id.img_qq_zone);

		mTextViewNoteContent = (TextView) findViewById(R.id.tv_note_content);
		mImageViewNotePath = (ImageView) findViewById(R.id.img_note_path);
		
		mLayoutWeChat = (LinearLayout) findViewById(R.id.ll_wechat);
		mLayoutFriends = (LinearLayout) findViewById(R.id.ll_friends);
		mLayoutWeibo = (LinearLayout) findViewById(R.id.ll_weibo);
		mLayoutQQ = (LinearLayout) findViewById(R.id.ll_qq);
		mLayoutQQZone = (LinearLayout) findViewById(R.id.ll_qq_zone);

		mImageViewWeiChat.setOnClickListener(this);
		mImageViewWeiChatMomments.setOnClickListener(this);
		mImageViewWeiBo.setOnClickListener(this);
		mImageViewQQ.setOnClickListener(this);
		mImageViewQZone.setOnClickListener(this);
		mButtonBack.setOnClickListener(this);

		bigAppIconVisible();
		 mTextViewNoteContent.setText("标题: "+note.getTitle()+"\n"+"内容: "+note.getContent()+"\n要分享的图片：");
		 Bitmap bitmap=PictureUtils.getBitmap(note.getPath(), 500, 500);
		 mImageViewNotePath.setImageBitmap(bitmap);
	}

	
	/**
	 * 让大图标显示
	 */
	private void bigAppIconVisible() {
		if (CheckAppExistUtils.checkAppExist(this, mPackageWeixin)) {

			mLayoutWeChat.setVisibility(View.VISIBLE);
			mLayoutFriends.setVisibility(View.VISIBLE);
		}
		if (CheckAppExistUtils.checkAppExist(this, mPackageWeibo)) {
			mLayoutWeibo.setVisibility(View.VISIBLE);
		}

		if (CheckAppExistUtils.checkAppExist(this, mPackageQQ)) {
			mLayoutQQ.setVisibility(View.VISIBLE);
		}
		if (CheckAppExistUtils.checkAppExist(this, mPackageQQZone)) {
			mLayoutQQZone.setVisibility(View.VISIBLE);
		}
//		mPackageQQZone
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_wechat:
			shareToDifferentPlatform(1);
			break;

		case R.id.img_friends:
			shareToDifferentPlatform(2);
			break;

		case R.id.img_weibo:
			shareToDifferentPlatform(3);
			break;

		case R.id.img_qq:
			shareToDifferentPlatform(4);
			break;

		case R.id.img_qq_zone:
			shareToDifferentPlatform(5);
			break;
		case R.id.btn_back_share:
			finish();
			break;

		default:
			break;
		}

	}

	/**
	 * 分享照片到不同平台
	 * @param whitch
	 */
	private void shareToDifferentPlatform(int whitch) {
		Platform pf = null;
		ShareParams sp = null;

		switch (whitch) {
		case 1:
			pf = ShareSDK.getPlatform(Wechat.NAME);
			sp = new ShareParams();

//			sp.setTitle("喵标题");//----什么玩意，不显示
//			sp.setText("喵文本");//----完全不显示，写了也白搭。
			sp.setImagePath(note.getPath());

			shareFail = "微信分享失败";
			shareCancel = "取消微信分享";
			break;

		case 2:
			pf = ShareSDK.getPlatform(WechatMoments.NAME);
			sp = new ShareParams();
			
//			sp.setTitle("喵标题");//完全没有用。
//			sp.setText(note.getContent());//有用
			sp.setImagePath(note.getPath());
			
			shareFail = "朋友圈分享失败";
			shareCancel = "取消朋友圈分享";
			break;

		case 3:
			pf = ShareSDK.getPlatform(SinaWeibo.NAME);
			sp = new ShareParams();
			
//			sp.setText(note.getTitle());//待验证
//			sp.setText(note.getContent());//待验证
			sp.setImagePath(note.getPath());
			
			shareFail = "微博分享失败";
			shareCancel = "取消微博分享";
			break;

		case 4:
			pf = ShareSDK.getPlatform(QQ.NAME);
			sp = new ShareParams();
			
//			sp.setText(note.getContent());//----会显示，有效果。
			sp.setImagePath(note.getPath());
			
			shareFail = "QQ分享失败";
			shareCancel = "取消QQ分享";
			break;

		case 5:
			pf = ShareSDK.getPlatform(QZone.NAME);
			sp = new ShareParams();
			
//			sp.setTitle(note.getTitle());//有用，显示
//			sp.setTitleUrl("http://www.qq.com/");//好像没有显示，没有用。
//			sp.setText(note.getContent());//有用，显示
			sp.setImagePath(note.getPath());
			
			shareFail = "QQ空间分享失败";
			shareCancel = "取消QQ空间分享";
			break;
		}
		// 设置分享事件的反馈
		pf.setPlatformActionListener(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Toast.makeText(ShareActivity.this, shareFail, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				Toast.makeText(ShareActivity.this, shareCancel, Toast.LENGTH_SHORT).show();
			}
		});
		pf.share(sp);
	}
	
	
	
}
