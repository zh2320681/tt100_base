package cn.tt100.base.imageLoader;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageBo {
	
	public File cachePath;
	public Activity ctx;
	/**
	 * 下载过程中 显示缺省图
	 */
	public Bitmap defaultImage;
	/**
	 * 下载过程中的 回掉
	 */
	public ImageHandler mHandler;
	/**
	 * 最后要显示的 view
	 */
	public ImageView mShowView;
	
	public String url;
	/**
	 * 下载完后  加载图片宽高 为-1的时候 加载原图大小
	 */
	public int afterLoadHeight;
	public int afterLoadWidth;

	public ImageBo(Activity paramActivity, String paramString,
			ImageView paramImageView) {
		this.ctx = paramActivity;
		this.url = paramString;
		this.mShowView = paramImageView;
		this.afterLoadWidth = -1;
		this.afterLoadHeight = -1;
	}

	public void clear() {
		this.ctx = null;
		this.url = null;
		this.mShowView = null;
		if (defaultImage != null && !defaultImage.isRecycled()){
			defaultImage.recycle();
		}	
		defaultImage = null;
		this.cachePath = null;
		this.mHandler = null;
	}
}
