package com.fly.perfectnotepad.adapter;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fly.perfectnotepad.NoteDetailAvtivity;
import com.fly.perfectnotepad.R;
import com.fly.perfectnotepad.base.BaseApplication;
import com.fly.perfectnotepad.entity.Note;
import com.fly.perfectnotepad.utils.PictureUtils;

public class NoteListAdapter extends BaseAdapter {

	private List<Note> mList;
	private Context mContext;

	// 播放音乐的时候会用到
	private MediaPlayer mPlayer = null;
	boolean mStartPlaying = true;

	public NoteListAdapter(Context context, List<Note> list) {
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.list_item, null);

		}
		final ViewHolder viewHolder;// 要强转，因为Tag里面是Object对象
		if (convertView.getTag() == null) {
			viewHolder = new ViewHolder();
			viewHolder.titleTextView = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.tv_content);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.typeTextView = (TextView) convertView
					.findViewById(R.id.tv_type);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.img_pic_or_video);
			viewHolder.videoImageView = (ImageView) convertView
					.findViewById(R.id.img_first_video);

			viewHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			viewHolder.voiceImageView = (ImageView) convertView
					.findViewById(R.id.img_voice);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.titleTextView.setText(mList.get(position).getTitle());
		viewHolder.contentTextView.setText(mList.get(position).getContent());
		viewHolder.timeTextView.setText(mList.get(position).getTime());
		viewHolder.typeTextView.setText(mList.get(position).getType());
		viewHolder.checkbox.setChecked(mList.get(position).getState());

		/*
		 * 还要做的是视频是不是显示，或者是隐藏。视频存在，那么视频的第一帧就存在，也就是pathOfFirstVedio存在且非空。
		 */
		String video = mList.get(position).getVideo();
		if (video == null) {
			viewHolder.videoImageView.setVisibility(View.GONE);
		} else {
			viewHolder.videoImageView.setVisibility(View.VISIBLE);
			String pathofVideo = mList.get(position).getPathOfFirstVideo();
			Bitmap bm = PictureUtils.getBitmap(pathofVideo, 100, 100);
			viewHolder.videoImageView.setImageBitmap(bm);
		}

		// 判断是不是显示语音
		String voicePath = mList.get(position).getVoice();
		if (voicePath == null) { 
			viewHolder.voiceImageView.setVisibility(View.GONE);
		} else {
			viewHolder.voiceImageView.setVisibility(View.VISIBLE);
			viewHolder.voiceImageView.setBackground(mContext.getResources()
					.getDrawable(R.drawable.voice));
		}

		// 用来判断是否显示图片。
		String path = mList.get(position).getPath();
		if (path == null) {
			viewHolder.imageView.setVisibility(View.GONE);
		} else {
				viewHolder.imageView.setVisibility(View.VISIBLE);
				Bitmap bm = PictureUtils.getBitmap(path, 100, 100);
				viewHolder.imageView.setImageBitmap(bm);
		}

		// 用来控制checkbox的显示或者是消失。
		if (BaseApplication.isVisibleFlag()) {
			viewHolder.checkbox.setVisibility(View.VISIBLE);
		} else {
			viewHolder.checkbox.setVisibility(View.INVISIBLE);
		}

		// 主页上的语音按钮，点击之后，会播放语音。。。设置点击事件
		viewHolder.voiceImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub，，根据路径播放语音。
				String voicePath = mList.get(position).getVoice();

				if (mStartPlaying) {
					mPlayer = new MediaPlayer();
					try {
						mPlayer.setDataSource(voicePath);// 根据路径找到录音，并播放。。
						mPlayer.prepare();
						mPlayer.start();
					} catch (IOException e) {
						Log.e("info", "prepare() failed");
					}
					;
				} else {
					mPlayer.release();
					mPlayer = null;
				}
				mStartPlaying = !mStartPlaying;

			}
		});

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (BaseApplication.isVisibleFlag()) {

					viewHolder.checkbox.toggle();// 改变状态
					if (viewHolder.checkbox.isChecked()) {
						mList.get(position).setState(true);
					} else {
						mList.get(position).setState(false);
					}
				} else {
					// 跳转页面。
					Intent intent = new Intent(mContext,
							NoteDetailAvtivity.class);
					String time = mList.get(position).getTime();
					intent.putExtra("time", time);
					mContext.startActivity(intent);
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView titleTextView;
		TextView contentTextView;
		TextView timeTextView;
		TextView typeTextView;
		CheckBox checkbox;
		ImageView imageView;// --用来显示图片，拍的照片。
		ImageView videoImageView;// --用来显示视频第一帧
		ImageView voiceImageView;

	}

}
