package cn.tt100.base.imageLoader;

import android.graphics.Bitmap;

public interface ImageHandler {

	/**
	 * ���ع����� �����쳣
	 * 
	 * @param bo
	 *            ����bo��
	 * @param mException
	 *            �����쳣
	 */
	public void downImgFail(ImageBo bo, Exception mException);

	/**
	 * �������
	 * @param bo ����bo��
	 */
	public void handlerDownLoadImg(ImageBo bo);

	/**
	 * ���ر��ص�ͼƬ �쳣
	 * @param bo
	 * @param mException
	 */
	public void loadLoacImgFail(ImageBo bo, Exception mException);

	
	public void moneryRecycleImage(String url, Bitmap paramBitmap);
}
