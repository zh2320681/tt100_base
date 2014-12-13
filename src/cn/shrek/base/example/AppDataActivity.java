package cn.shrek.base.example;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.example.appData.MyAppData;
import cn.shrek.base.ormlite.foreign.MappingType;
import cn.shrek.base.ui.ZWActivity;

public class AppDataActivity extends ZWActivity {
	@AutoInject(idFormat="ad_?")
	TextView tvShow;
	
	@AutoInject(idFormat="ad_?",clickSelector="myClick")
	Button btn1, btn2, btn3,btn4,btn5;
	
	MyAppData appData;
	
	OnClickListener myClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0 == btn1) {
				tvShow.setText("=======>" + appData.acount);
			} else if (arg0 == btn2) {
				tvShow.setText("=======>" + appData.password);
			} else if (arg0 == btn3) {
				tvShow.setText("=======>" + appData.isSave);
			} else if(arg0 == btn4){
				appData.acount = "张三李思王五";
				appData.password = "password1";
				appData.parInt = 12580;
				appData.eumnType = MappingType.ONE_TO_MANY;
				appData.saveData();
			} else if(arg0 == btn5){
				initialize();
			}
		}
	};
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		appData = new MyAppData(getApplicationContext());
		tvShow.setText("=======>" + appData.toString());
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
