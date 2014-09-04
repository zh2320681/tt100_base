package cn.shrek.base.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.example.bean.Company;
import cn.shrek.base.example.bean.Employee;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.TableInfo;
import cn.shrek.base.ormlite.ZWDBHelper;
import cn.shrek.base.ormlite.dao.DBDao;
import cn.shrek.base.ormlite.stmt.DeleteBuider;
import cn.shrek.base.ormlite.stmt.QueryBuilder;
import cn.shrek.base.ormlite.stmt.UpdateBuider;
import cn.shrek.base.ormlite.task.DBAsyncTask;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.ZWLogger;

public class DBTestActivity extends ZWActivity {

	@AutoInject(idFormat = "dt_?", clickSelector = "mClick")
	private Button createBtn, dropBtn, insertBtn, mergeInsertBtn,
			mergeInsertBtn1, delConBtn, delAllBtn, updateAllBtn, updateConBtn,
			updateMapBtn, queryAllBtn, queryAllBtn1, conQueryManyAllBtn,conQueryOneAllBtn,queryConBtn,
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
			} else if (v == dropBtn) {
				SQLiteDatabase mDatabase = mZWDBHelper.getDatabase(false);
				DBUtil.dropTable(mDatabase,
						TableInfo.newInstance(Company.class));
				DBUtil.dropTable(mDatabase,
						TableInfo.newInstance(Employee.class));
			} else if (v == insertBtn) {

				List<Company> coms = new ArrayList<Company>();
				for (int i = 101; i < 130; i++) {
					Company company = new Company();
					company.comId = i;
					company.companyName = "天天" + i + "网络科技";
					company.info = "好公司";
					company.isITCompany = true;

					coms.add(company);
				}

				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				comDao.insertObjs(coms);
			} else if (v == mergeInsertBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				List<Employee> coms = new ArrayList<Employee>();

				Company company = new Company();
				company.comId = 100;
				company.companyName = "天天一百网络科技";
				company.info = "好公司";
				company.isITCompany = true;

				for (int i = 0; i < 30; i++) {
					Employee e = new Employee();
					e.id = i;
					e.company = company;
					e.name = "钱骚货" + i + "号";
					e.isNew = (i % 2 == 1);
					coms.add(e);
				}
				long optNum = comDao.insertObjs(true, coms);
				showToast(optNum);
			} else if (v == mergeInsertBtn1) {
				// 多表 一对多 插入
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				List<Employee> coms = new ArrayList<Employee>();

				Company company = new Company();
				company.comId = 100;
				company.companyName = "天天一百网络科技";
				company.info = "好公司";
				company.isITCompany = true;

				for (int i = 0; i < 30; i++) {
					Employee e = new Employee();
					e.id = i;
					e.name = "钱骚货" + i + "号";
					e.isNew = (i % 2 == 1);
					coms.add(e);
				}

				company.allWorks = coms;

				long optNum = comDao.insertObjs(true, company);
				showToast(optNum);
			} else if (v == delConBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				DeleteBuider deleteBuider = comDao.deleteBuider();
				deleteBuider.leftBrackets().between("id", 10, 15).and()
						.like("name", "骚货", true, true).rightBrackets().or()
						.in("id", 22, 24, 26);
				ZWLogger.printLog(DBTestActivity.this, "删除SQL测试:::"
						+ deleteBuider.getSql());
				long optNum = comDao.deleteObjs(deleteBuider);
				showToast(optNum);
			} else if (v == delConAliBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				DeleteBuider deleteBuider = comDao.deleteBuider();
				deleteBuider.setTableAliases("A");
				deleteBuider.leftBrackets().between("id", 10, 15).and()
						.like("name", "骚货", true, true).rightBrackets().or()
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

				DBDao<Employee> empDao = mZWDBHelper.getDao(Employee.class);
				optNum += empDao.deleteAll();
				showToast(optNum);
			} else if (v == updateAllBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				Company c = new Company();
				comDao.clearObj(c);
				c.info = "更新测试看看";
				long optNum = comDao.updateObj(c);
				showToast(optNum);
			} else if (v == updateConBtn) {
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				UpdateBuider<Employee> updateBuider = comDao.updateBuider();
				updateBuider.leftBrackets().between("id", 10, 15).and()
						.like("name", "骚货", true, true).rightBrackets().or()
						.in("id", 22, 24, 26);
				Employee e = new Employee();
				comDao.clearObj(e);
				e.name = "更新去除Boolean特殊";
				e.isExpired = true;
				updateBuider.addValue(e, "isITCompany");
				long optNum = comDao.updateObj(updateBuider);
				showToast(optNum);
			} else if (v == updateMapBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("createTime", new Date());
				long optNum = comDao.updateObj(map);
				showToast(optNum);
			} else if (v == updateCaceadeBtn) {
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", 99);
				long optNum = comDao.updateObj(map);
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
			} else if(v == conQueryManyAllBtn){
				//条件查询(包括多对一外键对象)
				DBDao<Employee> comDao = mZWDBHelper.getDao(Employee.class);
				QueryBuilder qBuilder = comDao.queryBuilder();
				qBuilder.in("id", 10, 15, 18);
				
				List<Employee> emps = comDao.queryJoinObjs(qBuilder);
				
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Employee e = emps.get(j);
					sb.append(j + ". " + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			}else if(v == conQueryOneAllBtn){
				//条件查询(包括一对多外键对象)
				DBDao<Company> comDao = mZWDBHelper.getDao(Company.class);
				List<Company> emps = comDao.queryJoinAllObjs();
				
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < emps.size(); j++) {
					Company e = emps.get(j);
					sb.append(j + ". " + e.toString() + "\n");
				}
				infoView.setText(sb.toString());
			}else if (v == queryAllBtn1) {
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
					protected Integer enforcerBackground(ZWDBHelper mHelper) {
						// TODO Auto-generated method stub
						DBDao<Employee> comDao = mZWDBHelper
								.getDao(Employee.class);
						List<Employee> coms = new ArrayList<Employee>();

						Company company = new Company();
						company.comId = 100;
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
				List<Employee> list = mZWDBHelper.queryObjs(
						"select * from Employee ", Employee.class);

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
			public Class<? extends ZWDatabaseBo>[] loadDatabaseClazz() {
				// TODO Auto-generated method stub
				return new Class[] { Company.class, Employee.class };
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
