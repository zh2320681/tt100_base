package cn.shrek.base.example.fragment;

import android.os.Bundle;
import android.widget.ImageView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.LayoutSelector;
import cn.shrek.base.ui.ZWFragment;
import cn.tt100.base.R;

@LayoutSelector(id=R.layout.fragment1)
public class TestFragment1 extends ZWFragment {
	
	@AutoInject(idFormat="f1_?")
	ImageView imageView;

	@Override
	public void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		imageView.setImageResource(R.drawable.a3);
	}

}
