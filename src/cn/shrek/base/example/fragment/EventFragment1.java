package cn.shrek.base.example.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.event.ZWEvent;
import cn.shrek.base.event.ZWEventBus;
import cn.shrek.base.example.bean.Employee;
import cn.shrek.base.ui.ZWFragment;
import cn.tt100.base.R;

@Controller(layoutId = R.layout.event_f1, idFormat = "ef1_?")
public class EventFragment1 extends ZWFragment {
	public static final String EVENT1 = "EVENT1";
	public static final String EVENT2 = "EVENT2";
	public static final String EVENT3 = "EVENT3";
	public static final String EVENT4 = "EVENT4";
	public static final String EVENT5 = "EVENT5";

	@AutoInject(clickSelector = "myClick")
	Button sendEventBtn1, sendEventBtn2, sendEventBtn3, sendEventBtn4,
			sendEventBtn5, sendEventBtn6, sendEventBtn7;

	@AutoInject
	ZWEventBus bus;

	OnClickListener myClick;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		myClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == sendEventBtn1) {
					bus.post(EVENT1);
				} else if (v == sendEventBtn2) {
					bus.post(EVENT2);
				} else if (v == sendEventBtn3) {
					Employee emp = new Employee();
					emp.name = "身古雅";
					emp.id = 1001;
					ZWEvent event = ZWEvent.obtainObj(
							ZWConstants.NULL_STR_VALUE,
							ZWConstants.NULL_INT_VALUE, emp);
					bus.post(event);
				} else if (v == sendEventBtn4) {
					Employee emp = new Employee();
					emp.name = "身古雅";
					emp.id = 1001;
					bus.post(EVENT3, 10023, emp);
				} else if (v == sendEventBtn5) {
					bus.post(102);
				} else if (v == sendEventBtn6) {
					bus.post(EVENT4);
				} else if (v == sendEventBtn7) {
					new Thread() {
						@Override
						public void run() {
							bus.post(EVENT5);
						};
					}.run();
				}
			}
		};
	}

	@Override
	public void onCreateView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

}
