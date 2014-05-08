package cn.tt100.base.example;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.tt100.base.ZWActivity;
import cn.tt100.base.ZWApplication;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;

public class MenuActivity extends ZWActivity {
	@AutoInitialize(idFormat = "menu_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button dbTestBtn,downTestBtn,imgBtn,restTestBtn,errorTestBtn,logPrintTestBtn;

	private OnClickListener mClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0 == dbTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), DBTestActivity.class);
			    startActivity(intent);
			}else if(arg0 == downTestBtn){
				
			}else if(arg0 == imgBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ImageTestActivity.class);
			    startActivity(intent);
			}else if(arg0 == restTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), RestActivity.class);
			    startActivity(intent);
			}else if(arg0 == errorTestBtn){
				throw new NullPointerException();
			}else if(arg0 == logPrintTestBtn){
				ZWApplication.isLoggerPrint = false;
				Toast.makeText(getApplicationContext(), "日志打印已关闭!", Toast.LENGTH_LONG).show();
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
