package cn.tt100.base.ormlite;

import cn.tt100.base.ZWApplication;
import cn.tt100.base.example.bean.Company;
import cn.tt100.base.example.bean.Employee;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ZWDBHelper extends SQLiteOpenHelper {

	private static DatabaseErrorHandler mErrorHandler = new DefaultDatabaseErrorHandler();
	
	public ZWDBHelper(Context context) {
		super(context, ZWApplication.dbName, null, ZWApplication.dbVersion, mErrorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		DBUtil.dropTable(arg0, Company.class);
		DBUtil.dropTable(arg0, Employee.class);
		
		onCreate(arg0);
	}

}
