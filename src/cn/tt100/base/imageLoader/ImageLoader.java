package cn.tt100.base.imageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.widget.ImageView;
import cn.tt100.base.R;
import cn.tt100.base.util.BaseUtil;
import cn.tt100.base.util.ZWLogger;

public class ImageLoader {
	private static ImageLoader mLoader;// ����

	static ExecutorService executorService;

	private static FileCache fileCache;// �ļ�����
	// ����imageview��Ҫ����URL
	private static Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private static MemoryCache memoryCache;// �ڴ滺��
	// ȱʡ����ʾid
	static int stub_id;

	/**
	 * ʵ��������
	 * 
	 * @param mApplication
	 *            ��application����
	 * @throws Exception
	 */
	private ImageLoader(Application mApplication) throws Exception {
		if (mApplication == null) {
			ZWLogger.printLog(ImageLoader.class, "appΪnull ���ܳ�ʼ��!");
			throw new Exception("appΪnull ���ܳ�ʼ��!");
		}
		memoryCache = new MemoryCache();

		File cacheDir = null;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"baseLoader");
		} else {
			cacheDir = mApplication.getCacheDir();
		}

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		fileCache = new FileCache(cacheDir);

//		stub_id = R.drawable.defaultpic;
	}

	/**
	 * �õ� ImageLoader
	 * 
	 * @return
	 */
	public static ImageLoader getLoader() {
		return getLoader(null);
	}

	/**
	 * �õ� ImageLoader
	 * 
	 * @param mApplication
	 * @return
	 */
	public static ImageLoader getLoader(Application mApplication) {
		if (mLoader == null) {
			try {
				mLoader = new ImageLoader(mApplication);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (executorService == null || executorService.isShutdown()
				|| executorService.isTerminated()) {
			executorService = Executors.newFixedThreadPool(2);
		}
		return mLoader;
	}

	/**
	 * ��ʵ����image
	 * 
	 * @param paramImageBo
	 */
	public void displayImage(ImageBo mImageBo) {
		imageViews.put(mImageBo.mShowView, mImageBo.url);
		if (mImageBo.cachePath != null) {
			fileCache.setCacheDir(mImageBo.cachePath);
		}

		Bitmap bitmap = memoryCache.get(mImageBo.url);
		if (bitmap != null) {
			showImgWithUserSize(mImageBo, bitmap);
			return;
		}

		// �ύ����
		PhotosLoader mLoader = new PhotosLoader(mImageBo);
		executorService.submit(mLoader);

		// ����Ĭ��ͼ
		if (mImageBo.defaultImage != null) {
			mImageBo.mShowView.setImageBitmap(mImageBo.defaultImage);
		} else {
			mImageBo.mShowView.setImageResource(stub_id);
		}

	}

	private static Bitmap getBitmap(ImageBo mImageBo) {
		File f = fileCache.getFile(mImageBo.url);
		if (f.exists()) {
			// from SD cache
			// Bitmap b =
			// CommonUtil.decodeFile(f,isShowList?listHeight:detailWidth);
			Bitmap b = null;
			try {
				b = BaseUtil.decodeBitmapWithOps(f.getPath(),
						mImageBo.afterLoadWidth, mImageBo.afterLoadHeight);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				if (mImageBo.mHandler == null){
					mImageBo.mHandler.loadLoacImgFail(mImageBo, e);
				}  
			}
			if (b != null) {
				return b;
			}
		}

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(mImageBo.url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			BaseUtil.CopyStream(is, os);
			os.close();
			conn.disconnect();

			// bitmap =
			// CommonUtil.decodeFile(f,isShowList?listHeight:detailWidth);
			bitmap = BaseUtil.decodeBitmapWithOps(f.getPath(),
					mImageBo.afterLoadWidth, mImageBo.afterLoadHeight);
			if (mImageBo.mHandler != null){
				mImageBo.mHandler.handlerDownLoadImg(mImageBo);
			}
			return bitmap;
		} catch (Exception ex) {
			ZWLogger.printLog(ImageLoader.class, "���ر��ػ���ʧ��!");
//			ex.printStackTrace();
			if (ex.getCause() instanceof OutOfMemoryError){
				memoryCache.clear();
			}
			if (mImageBo.mHandler != null){
				mImageBo.mHandler.loadLoacImgFail(mImageBo,ex);
			}
			return null;
		}
	}

	/**
	 * �ж�Ҫ���ص�ͼƬ �Ѿ���imageView����
	 * 
	 * @param photoToLoad
	 * @return true ��ռ�� falseδ��ռ��
	 */
	static boolean imageViewReused(ImageBo mImageBo) {
		String tag = imageViews.get(mImageBo.mShowView);
		if (tag != null && tag.equals(mImageBo.url)) {
			return false;
		}
		return true;
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public void destroyLoader() {
		imageViews.clear();
		executorService.shutdown();
	}

	/**
	 * �����û��ĳߴ��С ��ʵͼƬ
	 * 
	 * @param bo
	 * @param mBitmap
	 */
	private void showImgWithUserSize(ImageBo mBo, Bitmap mBitmap) {
		if (mBo.afterLoadHeight * mBo.afterLoadWidth == 1) {
			mBo.mShowView.setImageBitmap(mBitmap);
		} else {
			if (mBo.afterLoadHeight == -1) {
				mBo.afterLoadHeight = mBitmap.getHeight();
			}

			if (mBo.afterLoadWidth == -1) {
				mBo.afterLoadWidth = mBitmap.getWidth();
			}
			
			Bitmap newBitmap = ThumbnailUtils.extractThumbnail(mBitmap,
					mBo.afterLoadWidth, mBo.afterLoadHeight);
			mBo.mShowView.setImageBitmap(newBitmap);
		}
		mBo.clear();
		mBo = null;
	}

	class BitmapDisplayer implements Runnable {
		ImageBo mBo;
		Bitmap mBitmap;

		public BitmapDisplayer(ImageBo mImageBo, Bitmap mBitmap) {
			this.mBo = mImageBo;
			this.mBitmap = mBitmap;
		}

		public void run() {
			if (imageViewReused(this.mBo)) {
				this.mBo.clear();
				this.mBo = null;
			}

			if (mBitmap != null) {
				showImgWithUserSize(mBo, mBitmap);
				return;
			}
			// ���ص� bitmapΪnull
			if (mBo.defaultImage != null) {
				mBo.mShowView.setImageBitmap(mBo.defaultImage);
			} else {
				mBo.mShowView.setImageResource(stub_id);
			}
		}
	}

	class PhotosLoader implements Runnable {
		ImageBo bo;

		PhotosLoader(ImageBo bo) {
			this.bo = bo;
		}

		public void run() {
			try {
				if (imageViewReused(bo)) {
					return;
				}
				Bitmap bmp = getBitmap(bo);
				memoryCache.put(bo.url, bmp);
				if (imageViewReused(bo)) {
					return;
				}
				// �첽��ʾͼƬ
				BitmapDisplayer bd = new BitmapDisplayer(bo, bmp);
				Activity act = bo.weakCtx.get();
				if(act != null){
					act.runOnUiThread(bd);
				}
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}
}
