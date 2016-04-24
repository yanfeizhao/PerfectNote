package com.fly.perfectnotepad;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fly.perfectnotepad.adapter.NoteListAdapter;
import com.fly.perfectnotepad.adapter.SpinnerAdapter;
import com.fly.perfectnotepad.adapter.SpinnerAdapter.OnTypeButtonClickListener;
import com.fly.perfectnotepad.base.BaseApplication;
import com.fly.perfectnotepad.db.NoteDBHelper;
import com.fly.perfectnotepad.entity.Note;
import com.fly.perfectnotepad.utils.DBOperatorUtils;

public class MainActivity extends Activity implements OnClickListener ,OnTypeButtonClickListener{

	private List<Note> mList;
	private ListView mTextListView;
	static NoteListAdapter mNoteListAdapter;

	private Button mAddItemButton;
	public static TextView mContentTypeTextView;
	public static TextView mSelectTypeTextView;

	private SQLiteDatabase mSqLiteDatabase;
	private NoteDBHelper mNoteDBHelper;
	private ContentValues mValues;

	private List<String> mContentTypeList;// 语音，视频
	private List<String> mNoteTypeList;// 学习，生活

	public static PopupWindow mPopupWindow;
	private SpinnerAdapter mAdapter;


	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		initData();
		initView();
	}

	private void initData() {
		mNoteDBHelper = new NoteDBHelper(this, "notes.db");
		mSqLiteDatabase = mNoteDBHelper.getWritableDatabase();
		mValues = new ContentValues();
		mList = new ArrayList<Note>();// 因为在resume()里new的，所以mList不会有

		mContentTypeList = new ArrayList<String>();
		mNoteTypeList = new ArrayList<String>();
		String[] contenttype = new String[] { "语音", "视频", "图片",  "所有媒体" };
		for (int i = 0; i < contenttype.length; i++) {
			mContentTypeList.add(contenttype[i]);
		}

		String[] type = new String[] {  "工作类", "学习类", "生活类","情感类" ,"其他","全部类型" };
		for (int i = 0; i < type.length; i++) {
			mNoteTypeList.add(type[i]);
		}

	}

	private void initView() {

		mAddItemButton = (Button) findViewById(R.id.btn_add);
		mContentTypeTextView = (TextView) findViewById(R.id.btn_showAll);
//		mEditMoreButton = (Button) findViewById(R.id.btn_editLots);
		mSelectTypeTextView = (TextView) findViewById(R.id.btn_notetype);

		mAddItemButton.setOnClickListener(this);
		mContentTypeTextView.setOnClickListener(this);
//		mEditMoreButton.setOnClickListener(this);
		mSelectTypeTextView.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add:
			// 跳转到添加页面。
			Intent intent = new Intent(this, AddNoteActivity.class);
			startActivity(intent);
			dismiss();//返回回来的时候默认是隐藏checkbox的。
			break;
		case R.id.btn_showAll:
			showListView(v);
			updateList();
			break;

		case R.id.btn_notetype:
			showListView(v);
			updateList();
			break;
			
		default:
			break;
		}
	}

	/**
	 * 从数据库里查询数据，更新private List<Note> mList;，，刷新listView。。。notify。。。
	 */
	private void updateList() {
		// TODO Auto-generated method stub
		String contenttype = mContentTypeTextView.getText().toString();
		String notetype = mSelectTypeTextView.getText().toString();
//		BaseApplication.getInstance().showCustomToast(
//				"根据设定的条件，数据库进行查询。。" + notetype + "--" + contenttype);

	}

	@SuppressWarnings("deprecation")
	private void showListView(View v) {

		int id = v.getId();
		View view = View.inflate(this, R.layout.popupwindow_list, null);
		int width = mContentTypeTextView.getWidth();
		mPopupWindow = new PopupWindow(view, width,
				ViewGroup.LayoutParams.WRAP_CONTENT);// 让平popwindow的高度自适应。

		// 点击任何位置，mPopupWindow要消失：[以下两个要搭配使用] -----在显示之前设置属性
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 缺少这个，下面的会失效
		mPopupWindow.setOutsideTouchable(true);

		initListView(view, id);

		// 显示在某个控件的下面，这里是btn_spinner下面
		switch (id) {
		case R.id.btn_showAll:
			mPopupWindow.showAsDropDown(mContentTypeTextView);
			break;
		case R.id.btn_notetype:
			mPopupWindow.showAsDropDown(mSelectTypeTextView);
			break;

		default:
			break;
		}

	}

	private void initListView(View view, int id) {
		// TODO Auto-generated method stub
		ListView listView = (ListView) view.findViewById(R.id.lv_spinner);

		switch (id) {
		case R.id.btn_showAll:
			mAdapter = new SpinnerAdapter(this, mContentTypeList, id);
			listView.setAdapter(mAdapter);
			mAdapter.setOnTypeButtonClickListener(this);
			break;
		case R.id.btn_notetype:
			mAdapter = new SpinnerAdapter(this, mNoteTypeList, id);
			listView.setAdapter(mAdapter);
			mAdapter.setOnTypeButtonClickListener(this);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mList.clear();
		String media=mContentTypeTextView.getText().toString();
		String type=mSelectTypeTextView.getText().toString();
		
		mList = DBOperatorUtils.queryNoteByType(mSqLiteDatabase, type, media);
//		mList = DBOperatorUtils.queryNotes(mSqLiteDatabase);
		for (int i = 0; i < mList.size(); i++) {
			mList.get(i).setState(false);
		}
  
		mTextListView = (ListView) findViewById(R.id.lv_content);
		mNoteListAdapter = new NoteListAdapter(this, mList);
		mTextListView.setAdapter(mNoteListAdapter);
		// mNoteListAdapter.setOnShowListener(this);
		registerForContextMenu(mTextListView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 0, 0, "选择若干");
		menu.add(0, 1, 4, "确定删除");
		menu.add(0, 2, 1, "全选");
		menu.add(0, 3, 2, "全不选");
		menu.add(0, 4, 3, "取消");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: // 选择若干
			editMore();
			break;
		case 1:
			// 确定删除
			deleteMore();
			break;
		case 2:
			// 全选
			selectAll();
			break;
		case 3:
			// 全不选
			selectNull();
			break;
		case 4:
			// 取消--取消显示checkbox
			dismiss();
			break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	private void selectNull() {
		if (BaseApplication.isVisibleFlag()) {
			for (int i = 0; i < mList.size(); i++) {
				mList.get(i).setState(false);
			}
		} else {
			BaseApplication.getInstance().showCustomToast("亲，您还没有开始编辑。。");
		}
		mNoteListAdapter.notifyDataSetInvalidated();
	}

	private void selectAll() {
		if (!BaseApplication.isVisibleFlag()) {
			BaseApplication.setVisibleFlag(true);
		}
		for (int i = 0; i < mList.size(); i++) {
			mList.get(i).setState(true);
		}
		mNoteListAdapter.notifyDataSetInvalidated();
	}

	private void dismiss() {
		if (BaseApplication.isVisibleFlag()) {
			BaseApplication.setVisibleFlag(false);
			for (int i = 0; i < mList.size(); i++) {
				mList.get(i).setState(false);
			}
		}
		mNoteListAdapter.notifyDataSetInvalidated();
	}

	/**
	 * 删除选中 的所有的项目。
	 */
	private void deleteMore() {
		if (mList.size() <= 0) {
			return;
		}
		if (BaseApplication.isVisibleFlag()) {
			for (int location = 0; location < mList.size(); location++) {
				boolean flag = mList.get(location).getState();
				if (flag) {
					String time = mList.get(location).getTime();
					DBOperatorUtils.deleteContent(mSqLiteDatabase, time);
					// mList.remove(location);---写在这里会出错，因为移除一个长度就会改变，会出现删除紊乱的状况。
					continue;
				}
			}
			mList.clear();
			mList = DBOperatorUtils.queryNotes(mSqLiteDatabase);
			for (int i = 0; i < mList.size(); i++) {
				mList.get(i).setState(false);
			}

			mTextListView = (ListView) findViewById(R.id.lv_content);
			mNoteListAdapter = new NoteListAdapter(this, mList);
			mTextListView.setAdapter(mNoteListAdapter);

		}

	}

	/**
	 * 批量编辑
	 */
	private void editMore() {
		if (!BaseApplication.isVisibleFlag())
			BaseApplication.setVisibleFlag(true);
		mNoteListAdapter.notifyDataSetInvalidated();
	}

	@Override
	public void dealQueryByType() {
		mList.clear();
		String media=mContentTypeTextView.getText().toString();
		String type=mSelectTypeTextView.getText().toString();
		
		mList = DBOperatorUtils.queryNoteByType(mSqLiteDatabase, type, media);
		for (int i = 0; i < mList.size(); i++) {
			mList.get(i).setState(false);
		}
  
		mTextListView = (ListView) findViewById(R.id.lv_content);
		mNoteListAdapter = new NoteListAdapter(this, mList);
		mTextListView.setAdapter(mNoteListAdapter);
	}
}
