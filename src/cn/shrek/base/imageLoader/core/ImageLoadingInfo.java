package cn.shrek.base.imageLoader.core;

import java.util.concurrent.locks.ReentrantLock;

import cn.shrek.base.imageLoader.core.assist.ImageSize;
import cn.shrek.base.imageLoader.core.imageaware.ImageAware;
import cn.shrek.base.imageLoader.core.listener.ImageLoadingListener;
import cn.shrek.base.imageLoader.core.listener.ImageLoadingProgressListener;

/**
 * Information for load'n'display image task
 */
final class ImageLoadingInfo {

	final String uri;
	final String memoryCacheKey;
	final ImageAware imageAware;
	final ImageSize targetSize;
	final DisplayImageOptions options;
	final ImageLoadingListener listener;
	final ImageLoadingProgressListener progressListener;
	final ReentrantLock loadFromUriLock;

	public ImageLoadingInfo(String uri, ImageAware imageAware, ImageSize targetSize, String memoryCacheKey,
			DisplayImageOptions options, ImageLoadingListener listener,
			ImageLoadingProgressListener progressListener, ReentrantLock loadFromUriLock) {
		this.uri = uri;
		this.imageAware = imageAware;
		this.targetSize = targetSize;
		this.options = options;
		this.listener = listener;
		this.progressListener = progressListener;
		this.loadFromUriLock = loadFromUriLock;
		this.memoryCacheKey = memoryCacheKey;
	}
}
