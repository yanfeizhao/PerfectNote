package com.fly.perfectnotepad.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fly.perfectnotepad.AddNoteActivity;
import com.fly.perfectnotepad.MainActivity;
import com.fly.perfectnotepad.R;

public class SpinnerAdapter extends BaseAdapter {

	private List<String> list;
	private Context context;
	private int id;

	public SpinnerAdapter(Context context, List<String> list, int id) {
		this.list = list;
		this.context = context;
		this.id = id;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.popupwindow_list_item,
					null);
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();// 要强转，因为Tag里面是Object对象
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.msgTextView = (TextView) convertView
					.findViewById(R.id.tv_msg);
			convertView.setTag(viewHolder);
		}

		viewHolder.msgTextView.setText(list.get(position));

		// 这个是为listview的某个条目的点击事件 方法2
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				switch (id) {
				case R.id.btn_showAll:
					MainActivity.mContentTypeTextView.setText(list
							.get(position).toString());
					MainActivity.mPopupWindow.dismiss();
					mOnTypeButtonClickListener.dealQueryByType();
					break;
				case R.id.btn_notetype:
					MainActivity.mSelectTypeTextView.setText(list.get(position)
							.toString());
					MainActivity.mPopupWindow.dismiss();
					mOnTypeButtonClickListener.dealQueryByType();
					break;
				case R.id.btn_takePhoto:
					String photoTypeString = list.get(position).toString();
					// 消失之后判断点击的是哪一个，然后做相应的处理。--一个接口方法
					mListener.dealPhoteTypeButtonEvent(photoTypeString);
					break;
					
				case R.id.btn_changePhoto:
					String  photoType= list.get(position).toString();
					mListener.dealPhoteTypeButtonEvent(photoType);
					break;
				case R.id.tv_type:
					AddNoteActivity.mTypeTextView.setText(list
							.get(position).toString());
					AddNoteActivity.mPopupWindow.dismiss();
					break;
				default:
					break;
				}

			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView msgTextView;
	}

	// 处理点击发照片或者是更换照片按钮的事件。
	private onPhotoButtonClickListener mListener;
	public interface onPhotoButtonClickListener {
		void dealPhoteTypeButtonEvent(String type);
	}
	public void setOnPhotoButtonClickListener(
			onPhotoButtonClickListener listener) {
		mListener = listener;
	}
	
	private OnTypeButtonClickListener mOnTypeButtonClickListener;
	public interface OnTypeButtonClickListener{
		void dealQueryByType();
	}
	public void setOnTypeButtonClickListener(OnTypeButtonClickListener onTypeButtonClickListener){
		mOnTypeButtonClickListener=onTypeButtonClickListener;
	}

}
