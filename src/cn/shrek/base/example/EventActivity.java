package cn.shrek.base.example;

import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.example.fragment.EventFragment1;
import cn.shrek.base.example.fragment.EventFragment2;
import cn.shrek.base.example.fragment.EventFragment3;
import cn.shrek.base.ui.ZWFragmentActivity;
import cn.tt100.base.R;

@Controller(layoutId =R.layout.event)
public class EventActivity extends ZWFragmentActivity {

	@AutoInject(idFormat="ft_?")
	EventFragment1 fragment1;
	
	@AutoInject(idFormat="ft_?")
	EventFragment2 fragment2;
	
	@AutoInject(idFormat="ft_?")
	EventFragment3 fragment3;
	
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
