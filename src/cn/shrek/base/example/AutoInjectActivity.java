package cn.shrek.base.example;

import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.LayoutSelector;
import cn.shrek.base.example.bean.Company;
import cn.shrek.base.example.bean.Employee;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.ui.inject.Identity;
import cn.tt100.base.R;

@LayoutSelector(id = R.layout.inject)
public class AutoInjectActivity extends ZWActivity {
	public static final String TAG1 = "TAG1";
	public static final String TAG2 = "TAG2";

	@AutoInject(idFormat="inject_?")
	TextView infoView;
	
	@AutoInject(tag = TAG1)
	Company list,list1,list2;
	
	@AutoInject
	Company company;
	
	@AutoInject(tag = TAG2,isSingleInstance=true)
	Employee emp1,emp2,emp3;
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("list: "+list.toString()+"\n");
		sb.append("list1: "+list1.toString()+"\n");
		sb.append("list2: "+list2.toString()+"\n");
		sb.append("company: "+company.toString()+"\n");
		sb.append("emp1: "+emp1.toString()+"\n");
		sb.append("emp2: "+emp2.toString()+"\n");
		sb.append("emp3: "+emp3.toString()+"\n");
		sb.append("mIdentity: "+mIdentity.toString()+"\n");
		infoView.setText(sb.toString());
		
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
