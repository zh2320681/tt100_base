package cn.tt100.base.imageLoader;

import java.io.File;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageBo {

	public File cachePath;
	public WeakReference<Activity> weakCtx;
	/**
	 * ���ع����� ��ʾȱʡͼ
	 */
	public Bitmap defaultImage;
	/**
	 * ���ع����е� �ص�
	 */
	public ImageHandler mHandler;
	/**
	 * ���Ҫ��ʾ�� view
	 */
	public ImageView mShowView;

	public String url;
	/**
	 * ������� ����ͼƬ��� Ϊ-1��ʱ�� ����ԭͼ��С
	 */
	public int afterLoadHeight;
	public int afterLoadWidth;

	public ImageBo(Activity mActivity, String url, ImageView mImageView) {
		this.weakCtx = new WeakReference<Activity>(mActivity);
		this.url = url;
		this.mShowView = mImageView;
		this.afterLoadWidth = -1;
		this.afterLoadHeight = -1;
	}

	public void clear() {
		weakCtx.clear();
		weakCtx = null;
		this.url = null;
		this.mShowView = null;
		if (defaultImage != null && !defaultImage.isRecycled()) {
			defaultImage.recycle();
		}
		defaultImage = null;
		this.cachePath = null;
		this.mHandler = null;
	}
	
	
//	public Activity getActivity(){
//		return 
//	}
}
