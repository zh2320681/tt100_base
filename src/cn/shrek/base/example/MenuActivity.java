package cn.shrek.base.example;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.ui.ZWActivity;

public class MenuActivity extends ZWActivity {
	@AutoInject(idFormat = "menu_?",clickSelector = "mClick")
	private Button dbTestBtn,downTestBtn,imgBtn,restTestBtn,errorTestBtn,logPrintTestBtn,netTestBtn
		,fragmentTestBtn,listTestBtn;

	@AutoInject
	private LayoutInflater mInflater;
	
	private OnClickListener mClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0 == dbTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), DBTestActivity.class);
			    startActivity(intent);
			}else if(arg0 == downTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), DLTestActivity.class);
			    startActivity(intent);
				
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
				((ZWApplication)getApplication()).closeLoggerPrint();
				Toast.makeText(getApplicationContext(), "日志打印已关闭!", Toast.LENGTH_LONG).show();
			}else if(arg0 == netTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), NetTestActivity.class);
			    startActivity(intent);
			}else if(arg0 == fragmentTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), FragmentTestActivity.class);
			    startActivity(intent);
			}else if(arg0 == listTestBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ListTestActivity.class);
			    startActivity(intent);
			}
		}
	};

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		System.out.println("==================>"+mInflater);
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
