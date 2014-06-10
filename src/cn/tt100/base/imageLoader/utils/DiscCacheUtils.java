package cn.tt100.base.imageLoader.utils;

import java.io.File;

import cn.tt100.base.imageLoader.cache.disc.DiskCache;

/**
 * Utility for convenient work with disk cache.<br />
 * <b>NOTE:</b> This utility works with file system so avoid using it on application main thread.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
public final class DiscCacheUtils {

	private DiscCacheUtils() {
	}

	/** Returns {@link File} of cached image or <b>null</b> if image was not cached in disk cache */
	public static File findInCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() ? image : null;
	}

	/**
	 * Removed cached image file from disk cache (if image was cached in disk cache before)
	 *
	 * @return <b>true</b> - if cached image file existed and was deleted; <b>false</b> - otherwise.
	 */
	public static boolean removeFromCache(String imageUri, DiskCache diskCache) {
		File image = diskCache.get(imageUri);
		return image != null && image.exists() && image.delete();
	}
}
