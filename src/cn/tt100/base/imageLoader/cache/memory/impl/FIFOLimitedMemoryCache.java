package cn.tt100.base.imageLoader.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import cn.tt100.base.imageLoader.cache.memory.LimitedMemoryCache;

/**
 * Limited {@link Bitmap bitmap} cache. Provides {@link Bitmap bitmaps} storing. Size of all stored bitmaps will not to
 * exceed size limit. When cache reaches limit size then cache clearing is processed by FIFO principle.<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 */
public class FIFOLimitedMemoryCache extends LimitedMemoryCache {

	private final List<Bitmap> queue = Collections.synchronizedList(new LinkedList<Bitmap>());

	public FIFOLimitedMemoryCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (super.put(key, value)) {
			queue.add(value);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void remove(String key) {
		Bitmap value = super.get(key);
		if (value != null) {
			queue.remove(value);
		}
		super.remove(key);
	}

	@Override
	public void clear() {
		queue.clear();
		super.clear();
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected Bitmap removeNext() {
		return queue.remove(0);
	}

	@Override
	protected Reference<Bitmap> createReference(Bitmap value) {
		return new WeakReference<Bitmap>(value);
	}
}
