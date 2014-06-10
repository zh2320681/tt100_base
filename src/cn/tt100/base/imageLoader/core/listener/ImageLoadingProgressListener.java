package cn.tt100.base.imageLoader.core.listener;

import android.view.View;

/**
 * Listener for image loading progress.
 */
public interface ImageLoadingProgressListener {

	/**
	 * Is called when image loading progress changed.
	 *
	 * @param imageUri Image URI
	 * @param view     View for image. Can be <b>null</b>.
	 * @param current  Downloaded size in bytes
	 * @param total    Total size in bytes
	 */
	void onProgressUpdate(String imageUri, View view, int current, int total);
}
