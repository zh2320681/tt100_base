package cn.tt100.base.example.bean;

public class GalleryDate {
	
	public int id;
	public String name;
	public String pic;
	public String brief;
	public String publishtime;
	
	public GalleryDate(){
		super();
	}

	@Override
	public String toString() {
		return "GalleryDate [id=" + id + ", name=" + name + ", pic=" + pic
				+ ", brief=" + brief + ", publishtime=" + publishtime + "]";
	}
	
	
	
}
