package cn.tt100.base.example;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.widget.ImageView;
import cn.tt100.base.BaseActivity;
import cn.tt100.base.R;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.imageLoader.ImageBo;
import cn.tt100.base.imageLoader.ImageHandler;
import cn.tt100.base.imageLoader.ImageLoader;

public class ImageTestActivity extends BaseActivity {

	@AutoInitialize(idFormat = "it_?")
	private ImageView imgView;

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		ImageBo mImageBo = new ImageBo(
				this,
				"http://e.hiphotos.baidu.com/image/w%3D2048/sign=3e994137718da9774e2f812b8469f819/8b13632762d0f703e58617d80afa513d2697c5fa.jpg",
				imgView);
		mImageBo.afterLoadHeight = 200;
		mImageBo.afterLoadWidth = 400;
//		mImageBo.cachePath = new File(
//				Environment.getExternalStorageDirectory(), "²âÊÔ¿´¿´");
//		mImageBo.cachePath.mkdir();
		Bitmap mBitmap = ((BitmapDrawable) getResources().getDrawable(
				R.drawable.ic_launcher)).getBitmap();
		mImageBo.defaultImage = mBitmap;
		mImageBo.mHandler = new ImageHandler() {

			@Override
			public void moneryRecycleImage(String url, Bitmap paramBitmap) {
				// TODO Auto-generated method stub
				System.out.println("======>moneryRecycleImage");
			}

			@Override
			public void loadLoacImgFail(ImageBo bo, Exception mException) {
				// TODO Auto-generated method stub
				System.out.println("======>loadLoacImgFail");
				mException.printStackTrace();
			}

			@Override
			public void handlerDownLoadImg(ImageBo bo) {
				// TODO Auto-generated method stub
				System.out.println("======>handlerDownLoadImg");
			}

			@Override
			public void downImgFail(ImageBo bo, Exception mException) {
				// TODO Auto-generated method stub
				System.out.println("======>downImgFail");
				mException.printStackTrace();
			}
		};

		ImageLoader.getLoader().displayImage(mImageBo);
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
