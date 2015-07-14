package cn.shrek.base.example;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.ui.ZWActivity;

@Controller(idFormat="focus_?")
public class FocusActivity extends ZWActivity {

	@AutoInject
	EditText editView;
	
	@AutoInject(isKeyboardFocus=true)
	Button btn;
	
	@AutoInject
	ImageView imageView;
	
	@AutoInject(isKeyboardFocus=true)
	TextView labelView;
	
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
