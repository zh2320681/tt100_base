package cn.tt100.base.imageLoader.core.display;

import android.graphics.Bitmap;
import cn.tt100.base.imageLoader.core.assist.LoadedFrom;
import cn.tt100.base.imageLoader.core.imageaware.ImageAware;

/**
 * Just displays {@link Bitmap} in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware}
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer {
	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		imageAware.setImageBitmap(bitmap);
	}
}