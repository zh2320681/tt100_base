package cn.shrek.base.example.fragment;

import android.os.Bundle;
import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.annotation.Subscribe;
import cn.shrek.base.event.ThreadMode;
import cn.shrek.base.event.ZWEvent;
import cn.shrek.base.event.ZWEventBus;
import cn.shrek.base.example.bean.Company;
import cn.shrek.base.example.bean.Employee;
import cn.shrek.base.ui.ZWFragment;
import cn.tt100.base.R;

@Controller(layoutId = R.layout.event_f2,idFormat = "ef2_?")
public class EventFragment2 extends ZWFragment {

	@AutoInject
	TextView infoView;

	@AutoInject
	ZWEventBus bus;
	
	@Override
	public void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bus.register(this);
	}
	
	public void onDestroy() {
		super.onDestroy();
		bus.unregister(this);
	};
	
	@Subscribe(tag=EventFragment1.EVENT1)
	public void onEventTAG(){
		infoView.setText("111111111111111111111111");
	}
	
	@Subscribe(tag=EventFragment1.EVENT2)
	public void onEventTAGAndAutoInject(Company com,ZWEvent event){
		infoView.setText(com.toString()+"\n"+event.toString());
	}

	
	@Subscribe(flag = 102,isInjectParas=false)
	public void onEventObj(Company com){
		infoView.setText("2222222222222222\n NULL=====>"+com);
	}
	
	@Subscribe(tag=EventFragment1.EVENT3,flag = 10023)
	public void onEventALL(Company com,ZWEvent event,Employee emp){
		infoView.setText(com.toString()+"\n"+event.toString()+"\n"+emp.toString());
	}
	
	@Subscribe(tag=EventFragment1.EVENT4,threadMode=ThreadMode.BackgroundThread)
	public void onEventNotMain(){
		infoView.setText("非主线程改ui坑定报错!");
	}
	
	
	@Subscribe(tag=EventFragment1.EVENT5,threadMode=ThreadMode.PostThread)
	public void onEventMain(){
		infoView.setText("非主线程发给我,我主线程处理!");
	}
}
