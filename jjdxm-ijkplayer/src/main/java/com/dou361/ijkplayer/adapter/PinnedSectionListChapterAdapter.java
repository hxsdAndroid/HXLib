package com.dou361.ijkplayer.adapter;

import android.content.ClipData.Item;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dou361.ijkplayer.R;
import com.dou361.ijkplayer.bean.VideoijkChapter;
import com.dou361.ijkplayer.utils.DensityUtil;
import com.dou361.ijkplayer.widget.PinnedSectionListView;

import java.util.List;


/**
 * ========================================
 * 作 者：张明 
 * 版 本：1.0
 * <p>
 * 创建日期：2016/12/31
 * <p>
 * 描 述：阿里云视频播放器封装
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class PinnedSectionListChapterAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

	private Context mContext;

	private List<VideoijkChapter> videoChapterList;

	public PinnedSectionListChapterAdapter(Context m_context, List<VideoijkChapter> m_videoChapterList) {
		mContext = m_context;
		videoChapterList = m_videoChapterList;
	}

	@Override
	public int getCount() {
		if (videoChapterList != null) {
			return videoChapterList.size();
		}
		return 0;
	}

	@Override
	public VideoijkChapter getItem(int position) {
		if (videoChapterList != null) {
			return videoChapterList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	protected void prepareSections(int sectionsNumber) {
	}

	protected void onSectionAdded(Item section, int sectionPosition) {
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowview = convertView;
		ChapterViewHolder holder = null;
		if (rowview == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			rowview = inflater.inflate(R.layout.alivc_hxsd_player_chapter_list_item, null);
			holder = new ChapterViewHolder(rowview);
			rowview.setTag(holder);

		} else {
			holder = (ChapterViewHolder) rowview.getTag();
		}

		VideoijkChapter item = getItem(position);
		if (item.getType() == VideoijkChapter.SECTION) {
			holder.getTxtTitle().setText(item.getTitle());
			holder.getTxtTitle().setPadding(DensityUtil.dp2px(mContext,50),DensityUtil.dp2px(mContext,25),DensityUtil.dp2px(mContext,50),DensityUtil.dp2px(mContext,8));
			holder.rootView.setBackgroundColor(Color.parseColor("#99000000"));
		} else {
			holder.getTxtTitle().setText(item.getTitle());
			holder.rootView.setBackgroundColor(Color.parseColor("#30000000"));
			holder.getTxtTitle().setPadding(DensityUtil.dp2px(mContext,50),DensityUtil.dp2px(mContext,15),DensityUtil.dp2px(mContext,50),DensityUtil.dp2px(mContext,15));
		}
		holder.txtTitle.setSelected(item.isChoose());
//		if(item.isChoose()){
//			holder.getTxtTitle().setTextColor(Color.parseColor("#ff2f50"));
//		}else{
//			holder.getTxtTitle().setTextColor(Color.parseColor("#ffffff"));
//		}
		return rowview;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position) != null) {
			return getItem(position).getType();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == VideoijkChapter.SECTION;
	}

	class ChapterViewHolder {
		private View baseView;
		private View rootView;
		private TextView txtTitle;

		public ChapterViewHolder(View baseView) {
			this.baseView = baseView;
		}

		public TextView getTxtTitle() {
			if (txtTitle == null) {
				txtTitle = (TextView) baseView.findViewById(R.id.simple_player_chapter_name);
				rootView = baseView.findViewById(R.id.simple_player_chapter_root);
			}
			return txtTitle;
		}
	}
}
