package cn.shrek.base.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.example.bean.Company;
import cn.shrek.base.example.bean.Employee;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.ui.inject.CustomInstanceFactory;
import cn.shrek.base.ui.inject.Identity;
import cn.shrek.base.ui.inject.Injector;

public class MenuActivity extends ZWActivity {
	@AutoInject(idFormat = "menu_?",clickSelector = "mClick")
	private Button dbTestBtn,downTestBtn,imgBtn,restTestBtn,errorTestBtn,logPrintTestBtn,netTestBtn
		,fragmentTestBtn,listTestBtn,appDataBtn,injectBtn;

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
			}else if(arg0 == appDataBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AppDataActivity.class);
			    startActivity(intent);
			}else if(arg0 == injectBtn){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AutoInjectActivity.class);
			    startActivity(intent);
				
			}
		}
	};

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		Injector.instance().setCustomFactory(new CustomInstanceFactory() {
			
			@Override
			public Identity getInstanceByTag(String tag) {
				// TODO Auto-generated method stub
				if(tag.equals(AutoInjectActivity.TAG1)){
					Company com = new Company();
					com.companyName = "天天一百";
					com.id = new  Random().nextInt(1000);;
					return com;
				}else if(tag.equals(AutoInjectActivity.TAG2)){
					Employee com = new Employee();
					com.name = "张三";
					com.id = new  Random().nextInt(1000);;
					return com;
				}
				return null;
			}
			
			@Override
			public List<Identity> getDefaultInstance() {
				// TODO Auto-generated method stub
				List<Identity> list = new ArrayList<Identity>();
				Company com = new Company();
				com.companyName = "天天一百";
				com.id = 250;
				list.add(com);
				
				return list;
			}
		});
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
