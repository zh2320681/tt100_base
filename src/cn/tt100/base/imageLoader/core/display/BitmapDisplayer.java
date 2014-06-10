package cn.tt100.base.imageLoader.core.display;

import android.graphics.Bitmap;
import cn.tt100.base.imageLoader.core.assist.LoadedFrom;
import cn.tt100.base.imageLoader.core.imageaware.ImageAware;

/**
 * Displays {@link Bitmap} in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware}. Implementations can
 * apply some changes to Bitmap or any animation for displaying Bitmap.<br />
 * Implementations have to be thread-safe.
 */
public interface BitmapDisplayer {
	/**
	 * Displays bitmap in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware}.
	 * <b>NOTE:</b> This method is called on UI thread so it's strongly recommended not to do any heavy work in it.
	 *
	 * @param bitmap     Source bitmap
	 * @param imageAware {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view} to
	 *                   display Bitmap
	 * @param loadedFrom Source of loaded image
	 * ImageAware}
	 */
	void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom);
}
