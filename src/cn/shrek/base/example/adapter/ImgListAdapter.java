package cn.shrek.base.example.adapter;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.ui.ZWBaseAdapter;
import cn.shrek.base.ui.ZWHolderBo;
import cn.tt100.base.R;

@Controller(idFormat = "ili_?", layoutId = R.layout.img_list_item)
public class ImgListAdapter extends
		ZWBaseAdapter<String, ImgListAdapter.ImgListHolder> {

	DisplayImageOptions imageOptions;
	
	int selectIndex = 0;

	public ImgListAdapter(Context ctx) {
		super(ctx, ImgListAdapter.ImgListHolder.class);
		// TODO Auto-generated constructor stub
		dataSource = new ArrayList<String>();
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb11128c6a4c4a20a44623dc89.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e7bfdd3b2818ba61ea9d345d5.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf44a378291f1d3572c10dfcfeb.jpg");
		dataSource
				.add("http://h.hiphotos.baidu.com/image/pic/item/838ba61ea8d3fd1f8e177642334e251f95ca5fa8.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb10f08d6a4c4a20a44723dceb.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/0df431adcbef7609db324de72ddda3cc7cd99e89.jpg");
		dataSource
				.add("http://g.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d1097edd849223fb80e7bec9089.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd55fa63d8c52da81cb38db3dd5.jpg");
		dataSource
				.add("http://f.hiphotos.baidu.com/image/pic/item/3b292df5e0fe99250935bd2837a85edf8db17189.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a89dae5bbcc8134954092376bb.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/342ac65c10385343a8f5bf929113b07eca80884e.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/ac345982b2b7d0a2368b050cc9ef76094a369ae4.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/4b90f603738da9772a1d41e4b251f8198718e3cb.jpg");

		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb11128c6a4c4a20a44623dc89.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e7bfdd3b2818ba61ea9d345d5.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf44a378291f1d3572c10dfcfeb.jpg");
		dataSource
				.add("http://h.hiphotos.baidu.com/image/pic/item/838ba61ea8d3fd1f8e177642334e251f95ca5fa8.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb10f08d6a4c4a20a44723dceb.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/0df431adcbef7609db324de72ddda3cc7cd99e89.jpg");
		dataSource
				.add("http://g.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d1097edd849223fb80e7bec9089.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd55fa63d8c52da81cb38db3dd5.jpg");
		dataSource
				.add("http://f.hiphotos.baidu.com/image/pic/item/3b292df5e0fe99250935bd2837a85edf8db17189.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a89dae5bbcc8134954092376bb.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/342ac65c10385343a8f5bf929113b07eca80884e.jpg");

		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb11128c6a4c4a20a44623dc89.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e7bfdd3b2818ba61ea9d345d5.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf44a378291f1d3572c10dfcfeb.jpg");
		dataSource
				.add("http://h.hiphotos.baidu.com/image/pic/item/838ba61ea8d3fd1f8e177642334e251f95ca5fa8.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb10f08d6a4c4a20a44723dceb.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/0df431adcbef7609db324de72ddda3cc7cd99e89.jpg");
		dataSource
				.add("http://g.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d1097edd849223fb80e7bec9089.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd55fa63d8c52da81cb38db3dd5.jpg");
		dataSource
				.add("http://f.hiphotos.baidu.com/image/pic/item/3b292df5e0fe99250935bd2837a85edf8db17189.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a89dae5bbcc8134954092376bb.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/342ac65c10385343a8f5bf929113b07eca80884e.jpg");

		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb11128c6a4c4a20a44623dc89.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e7bfdd3b2818ba61ea9d345d5.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf44a378291f1d3572c10dfcfeb.jpg");
		dataSource
				.add("http://h.hiphotos.baidu.com/image/pic/item/838ba61ea8d3fd1f8e177642334e251f95ca5fa8.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb10f08d6a4c4a20a44723dceb.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/0df431adcbef7609db324de72ddda3cc7cd99e89.jpg");
		dataSource
				.add("http://g.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d1097edd849223fb80e7bec9089.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd55fa63d8c52da81cb38db3dd5.jpg");
		dataSource
				.add("http://f.hiphotos.baidu.com/image/pic/item/3b292df5e0fe99250935bd2837a85edf8db17189.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a89dae5bbcc8134954092376bb.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/342ac65c10385343a8f5bf929113b07eca80884e.jpg");

		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb11128c6a4c4a20a44623dc89.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e7bfdd3b2818ba61ea9d345d5.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf44a378291f1d3572c10dfcfeb.jpg");
		dataSource
				.add("http://h.hiphotos.baidu.com/image/pic/item/838ba61ea8d3fd1f8e177642334e251f95ca5fa8.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb10f08d6a4c4a20a44723dceb.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/0df431adcbef7609db324de72ddda3cc7cd99e89.jpg");
		dataSource
				.add("http://g.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d1097edd849223fb80e7bec9089.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd55fa63d8c52da81cb38db3dd5.jpg");
		dataSource
				.add("http://f.hiphotos.baidu.com/image/pic/item/3b292df5e0fe99250935bd2837a85edf8db17189.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a89dae5bbcc8134954092376bb.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/342ac65c10385343a8f5bf929113b07eca80884e.jpg");

		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb11128c6a4c4a20a44623dc89.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e7bfdd3b2818ba61ea9d345d5.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf44a378291f1d3572c10dfcfeb.jpg");
		dataSource
				.add("http://h.hiphotos.baidu.com/image/pic/item/838ba61ea8d3fd1f8e177642334e251f95ca5fa8.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb10f08d6a4c4a20a44723dceb.jpg");
		dataSource
				.add("http://a.hiphotos.baidu.com/image/pic/item/0df431adcbef7609db324de72ddda3cc7cd99e89.jpg");
		dataSource
				.add("http://g.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d1097edd849223fb80e7bec9089.jpg");
		dataSource
				.add("http://c.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd55fa63d8c52da81cb38db3dd5.jpg");
		dataSource
				.add("http://f.hiphotos.baidu.com/image/pic/item/3b292df5e0fe99250935bd2837a85edf8db17189.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/9825bc315c6034a89dae5bbcc8134954092376bb.jpg");
		dataSource
				.add("http://d.hiphotos.baidu.com/image/pic/item/342ac65c10385343a8f5bf929113b07eca80884e.jpg");
	}

	@Override
	public void optView(ImgListHolder tagHolder, String source, int position) {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(source, tagHolder.imgView, getDefaultOption());
		
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 200);
		tagHolder.getRootView().setLayoutParams(lp);
	}

	public void setSelectPosition(int index){
		this.selectIndex = index;
		notifyDataSetChanged();
	}
	
	class ImgListHolder extends ZWHolderBo {
		ImageView imgView;
		TextView txtView;
	}

	public DisplayImageOptions getDefaultOption() {
		if (imageOptions == null) {
			imageOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_launcher)
					// resource
					// or
					// drawable
					.showImageForEmptyUri(R.drawable.ic_launcher)
					// resource
					// or
					// drawable
					.showImageOnFail(R.drawable.ic_launcher)
					// resource
					// or
					// drawable
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return imageOptions;
	}
}
