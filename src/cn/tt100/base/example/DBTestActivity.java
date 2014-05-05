package cn.tt100.base.example;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.tt100.base.ZWActivity;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;
import cn.tt100.base.example.bean.Company;
import cn.tt100.base.example.bean.Employee;
import cn.tt100.base.ormlite.DBUtil;
import cn.tt100.base.ormlite.ZWDBHelper;

public class DBTestActivity extends ZWActivity {
	
	@AutoInitialize(idFormat = "dt_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button createBtn;

	ZWDBHelper mZWDBHelper;
	
	private OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == createBtn){
				SQLiteDatabase mDatabase = mZWDBHelper.getWritableDatabase();
				DBUtil.createTable(mDatabase, Employee.class, true);
				DBUtil.createTable(mDatabase, Company.class, true);
			}
		}
		
	};
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		mZWDBHelper = new ZWDBHelper(getApplicationContext());
		
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
