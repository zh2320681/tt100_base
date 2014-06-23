package cn.shrek.base.example;

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
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.example.bean.Company;
import cn.shrek.base.example.bean.Employee;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.ZWDBHelper;
import cn.shrek.base.ormlite.dao.DBDao;
import cn.shrek.base.ormlite.stmt.DeleteBuider;
import cn.shrek.base.ormlite.stmt.QueryBuilder;
import cn.shrek.base.ormlite.stmt.UpdateBuider;
import cn.shrek.base.ormlite.task.DBAsyncTask;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.ZWLogger;

public class DBTestActivity extends ZWActivity {

	@AutoInject(idFormat = "dt_?",clickSelector = "mClick")
	private Button createBtn, insertBtn, delConBtn, delAllBtn, updateAllBtn,
			updateConBtn, updateMapBtn, queryAllBtn, queryAllBtn1, queryConBtn,
			queryCountBtn, delConAliBtn, queryConAliBtn, queryJoinBtn,
			updateCaceadeBtn, deleteCaceadeBtn, asyncBtn, sqlToMapBtn,
			sqlToObjectBtn;

	@AutoInject(idFormat = "dt_?")
	private TextView infoView;

	ZWDBHelper mZWDBHelper;

	private OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == createBtn) {
				SQLiteDatabase mDatabase = mZWDBHelper.getDatabase(false);
				DBUtil.createTable(mDatabase, Company.class, true);
				DBUtil.createTable(mDatabase, Employee.class, true);
			} else if (v == insertBtn) {
				// DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				// List<Company> coms = new ArrayList<Company>();
				// for (int i = 0; i < 30; i++) {
				// Company c = new Company();
				// c.id = i;
				// c.companyName="公司名称"+i;
				// if(i%10 == 0){
				// c.info = StmtBuilder.NULL_STR;
				// }else{
				// c.info = "info"+i;
				// }
				//
				// c.isITCompany = (i%2==1);
				// coms.add(c);
				// }
				// long optNum = comDao.insertObjs(coms);
				// showToast(optNum);

				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				List<Employee> coms = new ArrayList<Employee>();

				Company company = new Company();
				company.id = 100;
				company.companyName = "天天一百网络科技";
				company.info = "好公司";
				company.isITCompany = true;

				DBDao<Company> companyDao = mZWDBHelper.getDao(Company.class);
				companyDao.insertObj(company);

				for (int i = 0; i < 30; i++) {
					Employee e = new Employee();
					e.id = i;
					e.company = company;
					e.name = "钱骚货" + i + "号";
					e.isNew = (i % 2 == 1);
					coms.add(e);
				}
				long optNum = comDao.insertObjs(coms);
				showToast(optNum);
			} else if (v == delConBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				DeleteBuider deleteBuider = comDao.deleteBuider();
				deleteBuider.leftBrackets().between("id", 10, 15).and()
						.like("info", "12", true, true).rightBrackets().or()
						.in("id", 22, 24, 26);
				ZWLogger.printLog(DBTestActivity.this, "删除SQL测试:::"
						+ deleteBuider.getSql());
				long optNum = comDao.deleteObjs(deleteBuider);
				showToast(optNum);
			} else if (v == delConAliBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				DeleteBuider deleteBuider = comDao.deleteBuider();
				deleteBuider.setTableAliases("A");
				deleteBuider.leftBrackets().between("id", 10, 15).and()
						.like("info", "12", true, true).rightBrackets().or()
						.in("id", 22, 24, 26);
				ZWLogger.printLog(DBTestActivity.this, "删除SQL测试:::"
						+ deleteBuider.getSql());
				long optNum = comDao.deleteObjs(deleteBuider);
				showToast(optNum);
			} else if (v == deleteCaceadeBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				long optNum = comDao.deleteAll();
				showToast(optNum);
			} else if (v == delAllBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				long optNum = comDao.deleteAll();
				showToast(optNum);
			} else if (v == updateAllBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				Company c = new Company();
				comDao.clearObj(c);
				c.info = "更新测试看看";
				long optNum = comDao.updateAllObjs(c);
				showToast(optNum);
			} else if (v == updateConBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				UpdateBuider<Company> updateBuider = comDao.updateBuider();
				updateBuider.leftBrackets().between("id", 10, 15).and()
						.like("info", "12", true, true).rightBrackets().or()
						.in("id", 22, 24, 26);
				Company c = new Company();
				comDao.clearObj(c);
				c.companyName = "更新去除Boolean特殊";
				c.isExpired = true;
				updateBuider.addValue(c, "isITCompany");
				long optNum = comDao.updateObjs(updateBuider);
				showToast(optNum);
			} else if (v == updateMapBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("createTime", new Date());
				long optNum = comDao.updateObjs(map);
				showToast(optNum);
			} else if (v == updateCaceadeBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", 99);
				long optNum = comDao.updateObjs(map);
				showToast(optNum);
			} else if (v == queryAllBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				List<Employee> emps = comDao.queryAllObjs();
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j + ". " + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			} else if (v == queryAllBtn1) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				List<Employee> emps = comDao
						.queryObjs("select a.*,b.* from Employee a LEFT JOIN _Company b on a.FK_Company_id = b._ID");
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j + ". " + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			} else if (v == queryConBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				QueryBuilder queryBuilder = comDao.queryBuilder();
				queryBuilder.compare("<=", "id", 10);
				queryBuilder.limitIndex = 5;
				queryBuilder.offsetIndex = 1;
				List<Employee> emps = comDao.queryObjs(queryBuilder);
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j + ". " + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			} else if (v == queryCountBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				QueryBuilder queryBuilder = comDao.queryBuilder();
				queryBuilder.compare("<=", "id", 10);
				queryBuilder.limitIndex = 5;
				queryBuilder.offsetIndex = 1;
				int num = comDao.queryCount(queryBuilder);
				infoView.setText("查询到的数量:" + num);
			} else if (v == queryConAliBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				QueryBuilder queryBuilder = comDao.queryBuilder();
				queryBuilder.setTableAliases("A");
				queryBuilder.compare("<=", "id", 10);
				queryBuilder.limitIndex = 5;
				queryBuilder.offsetIndex = 1;
				List<Employee> emps = comDao.queryObjs(queryBuilder);
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j + ". " + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			} else if (v == queryJoinBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				QueryBuilder queryBuilder = comDao.queryBuilder();
				queryBuilder.compare("<=", "id", 10);
				queryBuilder.limitIndex = 5;
				queryBuilder.offsetIndex = 1;
				List<Employee> emps = comDao.queryJoinObjs(queryBuilder);

				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j + ". " + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			} else if (v == asyncBtn) {
				DBAsyncTask task = new DBAsyncTask(mZWDBHelper, true) {

					@Override
					protected Integer doInBackground(ZWDBHelper mHelper,
							Object... arg0) {
						// TODO Auto-generated method stub
						DBDao<Employee> comDao = mZWDBHelper
								.getDao(Employee.class);
						List<Employee> coms = new ArrayList<Employee>();

						Company company = new Company();
						company.id = 100;
						company.companyName = "天天一百网络科技";
						company.info = "好公司";
						company.isITCompany = true;
						for (int i = 0; i < 100; i++) {
							Employee e = new Employee();
							e.id = i;
							e.company = company;
							e.name = "钱骚货" + i + "号";
							e.isNew = (i % 2 == 1);
							coms.add(e);
						}
						long optNum = comDao.insertObjs(coms);
						return (int) optNum;
					}

					@Override
					protected void onPostExecute(Integer result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						showToast(result);
					}
				};
				task.execute();
			} else if (v == sqlToMapBtn) {
				List<Map<String, Object>> list = mZWDBHelper
						.queryMaps("select * from Employee ");

				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < list.size(); j++) {
					Map<String, Object> e = list.get(j);
					sb.append(j + "====>" + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			} else if (v == sqlToObjectBtn) {
				List<Employee> list = mZWDBHelper
						.queryObjs("select * from Employee ",Employee.class);

				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < list.size(); j++) {
					Employee e = list.get(j);
					sb.append(j + "====>" + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			}
		}

	};

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		mZWDBHelper = new ZWDBHelper(getApplicationContext()) {

			@Override
			public void onCreate(SQLiteDatabase arg0) {
				// TODO Auto-generated method stub
				createTables(arg0, Company.class, Employee.class);
			}

			@Override
			public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				dropTables(arg0, Company.class, Employee.class);
				this.onCreate(arg0);
			}

		};

	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

	private void showToast(long num) {
		Toast.makeText(getApplicationContext(), num + "条记录被影响",
				Toast.LENGTH_SHORT).show();
	}

}
