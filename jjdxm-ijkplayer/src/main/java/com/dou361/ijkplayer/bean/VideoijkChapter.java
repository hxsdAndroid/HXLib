package com.dou361.ijkplayer.bean;

import java.io.Serializable;

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
public class VideoijkChapter implements Serializable{

	public static final int ITEM = 0;
	public static final int SECTION = 1;

	private int type;
	
	private int sectionPosition;
	private int listPosition;

	private int ChapterId;

	private String title;

	private String Url;

	private boolean isChoose;
	public VideoijkChapter(){
		
	}
	public VideoijkChapter(int mType, String mTitle, String mUrl){
		setType(mType);
		setTitle(mTitle);
		setUrl(mUrl);
	}
	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSectionPosition() {
		return sectionPosition;
	}

	public void setSectionPosition(int sectionPosition) {
		this.sectionPosition = sectionPosition;
	}

	public int getListPosition() {
		return listPosition;
	}

	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}

	public int getChapterId() {
		return ChapterId;
	}

	public void setChapterId(int chapterId) {
		ChapterId = chapterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}


	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean choose) {
		isChoose = choose;
	}
}
