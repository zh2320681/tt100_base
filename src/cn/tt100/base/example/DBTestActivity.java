package cn.tt100.base.example;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.tt100.base.ZWActivity;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;
import cn.tt100.base.example.bean.Company;
import cn.tt100.base.example.bean.Employee;
import cn.tt100.base.ormlite.DBUtil;
import cn.tt100.base.ormlite.ZWDBHelper;
import cn.tt100.base.ormlite.dao.DBDao;
import cn.tt100.base.ormlite.stmt.DeleteBuider;
import cn.tt100.base.ormlite.stmt.StmtBuilder;
import cn.tt100.base.util.ZWLogger;

public class DBTestActivity extends ZWActivity {
	
	@AutoInitialize(idFormat = "dt_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button createBtn,insertBtn,delConBtn,delAllBtn;

	ZWDBHelper mZWDBHelper;
	
	private OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == createBtn){
				SQLiteDatabase mDatabase = mZWDBHelper.getDatabase(false);
				DBUtil.createTable(mDatabase, Company.class, true);
				DBUtil.createTable(mDatabase, Employee.class, true);
			}else if(v == insertBtn){
				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
				List<Company> coms = new ArrayList<Company>();
				for (int i = 0; i < 30; i++) {
					Company c = new Company();
					c.id = i;
					c.companyName="公司名称"+i;
					if(i%10 == 0){
						c.info = StmtBuilder.NULL_STR;
					}else{
						c.info = "info"+i;
					}
					
					c.isITCompany = (i%2==1);
					coms.add(c);
				}
				long optNum = comDao.insertObjs(coms);
				showToast(optNum);
			}else if(v == delConBtn){
				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
				DeleteBuider deleteBuider = comDao.deleteBuider();
				deleteBuider.leftBrackets().between("id", 10,15).and().like("info", "12", true, true)
				.rightBrackets().or().in("id", 22,24,26);
				ZWLogger.printLog(DBTestActivity.this, "删除SQL测试:::"+deleteBuider.getSql());
				long optNum = comDao.deleteObjs(deleteBuider);
				showToast(optNum);
			}else if(v == delAllBtn){
				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
				long optNum = comDao.deleteAll();
				showToast(optNum);
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
	
	
	private void showToast(long num){
		Toast.makeText(getApplicationContext(), num+"条记录被影响", Toast.LENGTH_SHORT).show();
	}

}
