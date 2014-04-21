package cn.tt100.base.imageLoader;

import android.graphics.Bitmap;

public interface ImageHandler {

	/**
	 * 下载过程中 遇到异常
	 * 
	 * @param bo
	 *            下载bo类
	 * @param mException
	 *            返回异常
	 */
	public void downImgFail(ImageBo bo, Exception mException);

	/**
	 * 下载完成
	 * @param bo 下载bo类
	 */
	public void handlerDownLoadImg(ImageBo bo);

	/**
	 * 加载本地的图片 异常
	 * @param bo
	 * @param mException
	 */
	public void loadLoacImgFail(ImageBo bo, Exception mException);

	
	public void moneryRecycleImage(String url, Bitmap paramBitmap);
}
