package com.fly.perfectnotepad.entity;

import java.io.Serializable;


public class Note implements Serializable{
	private int id;     //id
	private String title;//标题
	private String content;// 文字内容
	private String path;//存储的图片的路径
	private String  video;// 储的视频的路径
	private String type;//该记录的类别：感情/生活/学习....
	private String voice;//语音S
	private String address;//定位的位置
	private String time;//记录的时间
	private boolean state;//便签的状态；true:被选中，将会被删除。false：没被选中，将不会被删除。
	private String pathOfFirstVideo;

	
	public String getPathOfFirstVideo() {
		return pathOfFirstVideo;
	}
	public void setPathOfFirstVideo(String pathOfFirstVideo) {
		this.pathOfFirstVideo = pathOfFirstVideo;
	}
	
	public boolean getState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public Note(){
		
	}
	

	public Note(String title, String content, String type, String time,String path,String video) {
		super();
		this.title = title;
		this.content = content;
		this.type = type;
		this.time = time;
		this.path=path;
		this.video=video;
	}
	
	public Note(String title, String content, String type, String time,String path,String video,String pathOfFirstVideo) {
		super();
		this.title = title;
		this.content = content;
		this.type = type;
		this.time = time;
		this.path=path;
		this.video=video;
		this.pathOfFirstVideo=pathOfFirstVideo;
	}

	
	public Note(String title, String content, String type, String time,String path,String video,String pathOfFirstVideo,String voice) {
		super();
		this.title = title;
		this.content = content;
		this.type = type;
		this.time = time;
		this.path=path;
		this.video=video;
		this.pathOfFirstVideo=pathOfFirstVideo;
		this.voice=voice;
	}
	
	
	public Note(String title, String content, String type, String time,String path,String video,String pathOfFirstVideo,String voice,String address) {
		super();
		this.title = title;
		this.content = content;
		this.type = type;
		this.time = time;
		this.path=path;
		this.video=video;
		this.pathOfFirstVideo=pathOfFirstVideo;
		this.voice=voice;
		this.address=address;
	}
	


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVoice() {
		return voice;
	}
	public void setVoice(String voice) {
		this.voice = voice;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	

}