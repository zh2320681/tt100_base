package cn.tt100.base.example;

import java.util.ArrayList;
import java.util.List;

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
import cn.tt100.base.ormlite.dao.DBDao;

public class DBTestActivity extends ZWActivity {
	
	@AutoInitialize(idFormat = "dt_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button createBtn,insertBtn;

	ZWDBHelper mZWDBHelper;
	
	private OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == createBtn){
				SQLiteDatabase mDatabase = mZWDBHelper.getDatabase(false);
				DBUtil.createTable(mDatabase, Company.class, true);
				DBUtil.createTable(mDatabase, Employee.class, true);
				SQLiteDatabase mDatabase1 = mZWDBHelper.getDatabase(false);
				
			}else if(v == insertBtn){
				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
				List<Company> coms = new ArrayList<Company>();
				for (int i = 0; i < 30; i++) {
					Company c = new Company();
					c.id = i;
					c.companyName="¹«Ë¾Ãû³Æ"+i;
					c.info = "info"+i;
					coms.add(c);
				}
				comDao.insertObjs(coms);
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
