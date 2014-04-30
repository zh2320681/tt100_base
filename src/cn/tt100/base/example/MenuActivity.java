package cn.tt100.base.example;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.tt100.base.BaseActivity;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;

public class MenuActivity extends BaseActivity {
	@AutoInitialize(idFormat = "menu_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button dbTestBtn,downTestBtn,imgBtn,restTestBtn;

	private OnClickListener mClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0 == dbTestBtn){
				
			}else if(arg0 == downTestBtn){
				
			}else if(arg0 == imgBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ImageTestActivity.class);
			    startActivity(intent);
			}else if(arg0 == restTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), RestActivity.class);
			    startActivity(intent);
			}
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
