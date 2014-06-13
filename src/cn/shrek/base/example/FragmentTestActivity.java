package cn.shrek.base.example;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.example.fragment.TestFragment1;
import cn.shrek.base.example.fragment.TestFragment2;
import cn.shrek.base.ui.ZWFragmentActivity;
import cn.tt100.base.R;

public class FragmentTestActivity extends ZWFragmentActivity {
	@AutoInject(idFormat="ft_?")
	TestFragment1 fragment;
	
	@AutoInject(idFormat="ft_?",clickSelector="myClick")
	Button addBtn;
	
	@AutoInject
	TestFragment2 f2;
	
	OnClickListener myClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			addFragment(R.id.ft_layout, f2);
		}
	};
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
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
