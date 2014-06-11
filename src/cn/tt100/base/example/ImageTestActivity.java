package cn.tt100.base.example;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.tt100.base.R;
import cn.tt100.base.ZWActivity;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;
import cn.tt100.base.annotation.LayoutSelector;
import cn.tt100.base.example.custom.MyImageView;
import cn.tt100.base.imageLoader.core.DisplayImageOptions;
import cn.tt100.base.imageLoader.core.ImageLoader;
import cn.tt100.base.imageLoader.core.assist.ImageSize;
import cn.tt100.base.imageLoader.core.listener.ImageLoadingProgressListener;
import cn.tt100.base.imageLoader.core.listener.SimpleImageLoadingListener;

@LayoutSelector(id=R.layout.image_test)
public class ImageTestActivity extends ZWActivity {

	@AutoInitialize(idFormat = "it_?")
	@AutoOnClick(clickSelector="myClick")
	private Button configSetBtn,normalLoadBtn,sizeLoadBtn,clearMCacheBtn,clearDiscCacheBtn,proLoadBtn;
	
	@AutoInitialize(idFormat = "it_?")
	private MyImageView imageView;
	
	DisplayImageOptions options;
	ImageLoader loader;
	
	String url = "http://e.hiphotos.baidu.com/image/w%3D2048/sign=3e994137718da9774e2f812b8469f819/8b13632762d0f703e58617d80afa513d2697c5fa.jpg";
	String bigUrl = "http://news.yzz.cn/public/images/100730/93_140710_1.jpg";
	private OnClickListener myClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == configSetBtn){
				options = new DisplayImageOptions.Builder()
		        .showImageOnLoading(R.drawable.defaultpic) // resource or drawable
		        .showImageForEmptyUri(R.drawable.donwload_icon) // resource or drawable
		        .showImageOnFail(R.drawable.alert2) // resource or drawable
		        .resetViewBeforeLoading(false)  // default
		        .cacheInMemory(true) // default
		        .cacheOnDisc(true) // default
		        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
		        .build();
			}else if(v == normalLoadBtn){
				loader.displayImage(url, imageView, options);
			}else if(v == sizeLoadBtn){
				loader.loadImage(url, new ImageSize(100, 100), options , new SimpleImageLoadingListener(){

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						super.onLoadingComplete(imageUri, view, loadedImage);
						imageView.setImageBitmap(loadedImage);
					}
					
				});
			}else if(v == clearMCacheBtn){
				loader.clearMemoryCache();
			}else if(v == clearDiscCacheBtn){
				loader.clearDiskCache();
			}else if(v == proLoadBtn){
				loader.clearMemoryCache();
				loader.clearDiskCache();
				loader.displayImage(bigUrl, imageView, options, null,new ImageLoadingProgressListener() {
					
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {
						// TODO Auto-generated method stub
						if(view instanceof MyImageView){
							MyImageView mMyImageView = (MyImageView)view;
							mMyImageView.setText(String.format("%.2f%%", current*1.0f/total));
							if(total == current){
								mMyImageView.hideText();
							}
						}
					}
				});
			}
		}
	};

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
//		ZWApplication app = (ZWApplication)getApplication();
//		app.
		loader = ImageLoader.getInstance();
	
		
//		ImageBo mImageBo = new ImageBo(
//				this,
//				"http://e.hiphotos.baidu.com/image/w%3D2048/sign=3e994137718da9774e2f812b8469f819/8b13632762d0f703e58617d80afa513d2697c5fa.jpg",
//				imgView);
//		mImageBo.afterLoadHeight = 200;
//		mImageBo.afterLoadWidth = 400;
//		mImageBo.cachePath = new File(
//				Environment.getExternalStorageDirectory(), "²âÊÔ¿´¿´");
//		mImageBo.cachePath.mkdir();
//		Bitmap mBitmap = ((BitmapDrawable) getResources().getDrawable(
//				R.drawable.ic_launcher)).getBitmap();
//		mImageBo.defaultImage = mBitmap;
//		mImageBo.mHandler = new ImageHandler() {
//			@Override
//			public void moneryRecycleImage(String url, Bitmap paramBitmap) {
//				// TODO Auto-generated method stub
//				System.out.println("======>moneryRecycleImage");
//			}
//			@Override
//			public void loadLoacImgFail(ImageBo bo, Exception mException) {
//				// TODO Auto-generated method stub
//				System.out.println("======>loadLoacImgFail");
//				mException.printStackTrace();
//			}
//			@Override
//			public void handlerDownLoadImg(ImageBo bo) {
//				// TODO Auto-generated method stub
//				System.out.println("======>handlerDownLoadImg");
//			}
//			@Override
//			public void downImgFail(ImageBo bo, Exception mException) {
//				// TODO Auto-generated method stub
//				System.out.println("======>downImgFail");
//				mException.printStackTrace();
//			}
//		};
//		ImageLoader.getLoader().displayImage(mImageBo);
	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

}
