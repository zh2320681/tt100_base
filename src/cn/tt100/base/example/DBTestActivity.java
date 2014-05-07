package cn.tt100.base.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
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
import cn.tt100.base.ormlite.stmt.QueryBuilder;
import cn.tt100.base.ormlite.stmt.UpdateBuider;
import cn.tt100.base.util.ZWLogger;

public class DBTestActivity extends ZWActivity {
	
	@AutoInitialize(idFormat = "dt_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button createBtn,insertBtn,delConBtn,delAllBtn,updateAllBtn,updateConBtn,updateMapBtn
		,queryAllBtn,queryAllBtn1,queryConBtn,queryCountBtn;
	
	@AutoInitialize(idFormat = "dt_?")
	private TextView infoView;
	
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
//				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
//				List<Company> coms = new ArrayList<Company>();
//				for (int i = 0; i < 30; i++) {
//					Company c = new Company();
//					c.id = i;
//					c.companyName="公司名称"+i;
//					if(i%10 == 0){
//						c.info = StmtBuilder.NULL_STR;
//					}else{
//						c.info = "info"+i;
//					}
//					
//					c.isITCompany = (i%2==1);
//					coms.add(c);
//				}
//				long optNum = comDao.insertObjs(coms);
//				showToast(optNum);
				
				DBDao<Employee> comDao =  mZWDBHelper.getDao(Employee.class);
				List<Employee> coms = new ArrayList<Employee>();
				
				Company company = new Company();
				company.id = 100;
				company.companyName = "天天一百网络科技";
				company.info="好公司";
				company.isITCompany=true;
				for (int i = 0; i < 30; i++) {
					Employee e = new Employee();
					e.id = i;
					e.company = company;
					e.name="钱骚货"+i+"号";
					e.isNew = (i%2==1);
					coms.add(e);
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
			}else if(v == updateAllBtn){
				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
				Company c = new Company();
				comDao.clearObj(c);
				c.info="更新测试看看";
				long optNum = comDao.updateAllObjs(c);
				showToast(optNum);
			}else if(v == updateConBtn){
				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
				UpdateBuider<Company> updateBuider = comDao.updateBuider();
				updateBuider.leftBrackets().between("id", 10,15).and().like("info", "12", true, true)
				.rightBrackets().or().in("id", 22,24,26);
				Company c = new Company();
				comDao.clearObj(c);
				c.companyName ="更新去除Boolean特殊";
				c.isExpired = true;
				updateBuider.addValue(c, "isITCompany");
				long optNum = comDao.updateObjs(updateBuider);
				showToast(optNum);
			}else if(v == updateMapBtn){
				DBDao<Company> comDao =  mZWDBHelper.getDao(Company.class);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("createTime", new Date());
				long optNum = comDao.updateObjs(map);
				showToast(optNum);
			}else if(v == queryAllBtn){
				DBDao<Employee> comDao =  mZWDBHelper.getDao(Employee.class);
				List<Employee> emps = comDao.queryAllObjs();
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j+". "+e.toString()+"\n");
				}
				infoView.setText(sb.toString());
			}else if(v == queryAllBtn1){
				DBDao<Employee> comDao =  mZWDBHelper.getDao(Employee.class);
				List<Employee> emps = comDao.queryObjs("select a.*,b.* from Employee a LEFT JOIN _Company b on a.FK_Company_id = b._ID");
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j+". "+e.toString()+"\n");
				}
				infoView.setText(sb.toString());
			}else if(v == queryConBtn){
				DBDao<Employee> comDao =  mZWDBHelper.getDao(Employee.class);
				QueryBuilder queryBuilder = comDao.queryBuilder();
				queryBuilder.compare("<=", "id", 10);
				queryBuilder.limitIndex = 5;
				queryBuilder.offsetIndex = 1;
				List<Employee> emps = comDao.queryObjs(queryBuilder);
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j+". "+e.toString()+"\n");
				}
				infoView.setText(sb.toString());
			}else if(v == queryCountBtn){
				DBDao<Employee> comDao =  mZWDBHelper.getDao(Employee.class);
				QueryBuilder queryBuilder = comDao.queryBuilder();
				queryBuilder.compare("<=", "id", 10);
				queryBuilder.limitIndex = 5;
				queryBuilder.offsetIndex = 1;
				int num = comDao.queryCount(queryBuilder);
				infoView.setText("查询到的数量:"+num);
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
