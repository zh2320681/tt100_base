package cn.shrek.base.download.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import cn.shrek.base.download.bo.DLTask;
import cn.shrek.base.download.bo.DLThreadTask;
import cn.shrek.base.ormlite.DBUtil;
import cn.shrek.base.ormlite.ZWDBHelper;
import cn.shrek.base.util.ZWLogger;

public class DLDatabaseHelper extends SQLiteOpenHelper {
	/**
	 * 数据库版本
	 */
	private static final int DATABASE_VERSION = 1;
	// 锁
	private final Object LOCK = new Object();

	private final String LOG_NAME;

	public DLDatabaseHelper(Context mContext) {
		super(mContext, "baseDB", null, 1);
		this.LOG_NAME = getClass().getName();
	}

	public void close() {
		super.close();
		// this.taskDao = null;
		// this.threadDao = null;
	}

	public void delete(int taskHashCode) {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getWritableDatabase();
			int num = mDatabase.delete("DLThreadTask", "taskHashCode = "+taskHashCode, null);
			ZWLogger.printLog(DLDatabaseHelper.class, "删除线程下载任务,删除条数:" + num);
			mDatabase.close();
		}
	}

	/**
	 * 得到所有下载任务
	 * 
	 * @return
	 */
	public List<DLTask> getAllTasks() {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getReadableDatabase();
			List<DLTask> allTasks = new ArrayList<DLTask>();
			Cursor cursor = mDatabase.query("DLTask", null, null, null, null,
					null, null);
			while (cursor.moveToNext()) {
				DLTask mDLTask = new DLTask();
				mDLTask.downLoadUrl = cursor.getString(cursor
						.getColumnIndex("downLoadUrl"));
				mDLTask.savePath = cursor.getString(cursor
						.getColumnIndex("savePath"));
				mDLTask.dlThreadNum = cursor.getInt(cursor
						.getColumnIndex("dlThreadNum"));
				mDLTask.totalSize = cursor.getLong(cursor
						.getColumnIndex("totalSize"));
				mDLTask.fileName = cursor.getString(cursor
						.getColumnIndex("fileName"));
				mDLTask.createTime = cursor.getLong(cursor
						.getColumnIndex("createTime"));
				mDLTask.setErrorMessage(cursor.getString(cursor
						.getColumnIndex("errorMessage")));
				allTasks.add(mDLTask);
			}
			cursor.close();
			mDatabase.close();
			return allTasks;
		}
	}

	/**
	 * 得到已经下载的任务
	 * 
	 * @return
	 */
	public List<DLTask> getDLOverTasks() {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getReadableDatabase();
			ArrayList<DLTask> allDLList = new ArrayList<DLTask>();
			Cursor taskCursor = mDatabase.query("DLTask", null, null, null,
					null, null, null);
			if (taskCursor.moveToNext()) {
				DLTask mDLTask = new DLTask();
				mDLTask.downLoadUrl = taskCursor.getString(taskCursor
						.getColumnIndex("downLoadUrl"));
				mDLTask.totalSize = taskCursor.getLong(taskCursor
						.getColumnIndex("totalSize"));
				Cursor threadCursor = mDatabase.query("DLThreadTask",
						new String[] { "SUM(hasDownloadLength)" },
						"taskHashCode = " + mDLTask.hashCode(), null, null,
						null, "threadId");
				if (threadCursor.moveToNext()) {
					int downSize = threadCursor.getInt(0);
					if (downSize == mDLTask.totalSize) {
						mDLTask.savePath = taskCursor.getString(taskCursor
								.getColumnIndex("savePath"));
						mDLTask.dlThreadNum = taskCursor.getInt(taskCursor
								.getColumnIndex("dlThreadNum"));
						mDLTask.fileName = taskCursor.getString(taskCursor
								.getColumnIndex("fileName"));
						mDLTask.createTime = taskCursor.getLong(taskCursor
								.getColumnIndex("createTime"));
						mDLTask.setErrorMessage(taskCursor.getString(taskCursor
								.getColumnIndex("errorMessage")));
						allDLList.add(mDLTask);
					}
					threadCursor.close();
				}
			}
			taskCursor.close();
			mDatabase.close();
			return allDLList;
		}
	}

	// public Dao<DLTask, Integer> getDLTaskDao() throws SQLException {
	// if (this.taskDao == null) {
	// Dao localDao = getDao(DLTask.class);
	// this.taskDao = localDao;
	// }
	// return this.taskDao;
	// }

	// public Dao<DLThreadTask, Integer> getDLThreadDao() throws SQLException {
	// if (this.threadDao == null) {
	// Dao localDao = getDao(DLThreadTask.class);
	// this.threadDao = localDao;
	// }
	// return this.threadDao;
	// }

	/**
	 * 得到已经下载的长度
	 * @param taskHaseCode
	 * @return
	 */
	public int getDownloadSizeByPath(int taskHaseCode) {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getReadableDatabase();
			int size = 0;
			Cursor cursor = mDatabase.query("DLThreadTask",
					new String[] { "sum(hasDownloadLength)" },
					"taskHashCode = " + taskHaseCode, null, null, null,
					"threadId");
			if (cursor.moveToNext()) {
				size = cursor.getInt(0);
			}
			cursor.close();
			if (mDatabase.isOpen()) {
				mDatabase.close();
			}
			return size;
		}
	}

	/**
	 * 得到下载所有线程
	 * @param taskHaseCode
	 * @return
	 */
	public Set<DLThreadTask> getDownloadTaskByPath(int taskHaseCode) {
		synchronized (LOCK) {
			Set<DLThreadTask> ttSet = new HashSet<DLThreadTask>();
			SQLiteDatabase mDatabase = getReadableDatabase();
			Cursor mCursor = mDatabase.query("DLThreadTask",
					null, "taskHashCode = " + taskHaseCode, null, null, null, "threadId");
			while (mCursor.moveToNext()) {
				DLThreadTask mDLThreadTask = new DLThreadTask();
				mDLThreadTask.idCode = mCursor.getInt(mCursor.getColumnIndex("idCode"));
				mDLThreadTask.breakPointPosition = mCursor.getLong(mCursor.getColumnIndex("breakPointPosition"));
				mDLThreadTask.costTime = mCursor.getInt(mCursor.getColumnIndex("costTime"));
				mDLThreadTask.downloadBlock = mCursor.getLong(mCursor.getColumnIndex("downloadBlock"));
				mDLThreadTask.hasDownloadLength = mCursor.getLong(mCursor.getColumnIndex("hasDownloadLength"));
				mDLThreadTask.threadId = mCursor.getInt(mCursor.getColumnIndex("threadId"));
				ttSet.add(mDLThreadTask);
			}
			mCursor.close();
			mDatabase.close();
			return ttSet;
		}
	}

	/**
	 * 通过下载路径 获取 数据库任务对象
	 * @param path
	 * @return
	 */
	public DLTask getTaskByPath(String path) {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getReadableDatabase();
			DLTask mDLTask = new DLTask();
			Cursor localCursor = mDatabase.query("DLTask", null,
					"downLoadUrl = '" + path + "'", null, null, null, null);
			if (localCursor.moveToNext()) {
				mDLTask.downLoadUrl = localCursor.getString(localCursor.getColumnIndex("downLoadUrl"));
				mDLTask.savePath = localCursor.getString(localCursor.getColumnIndex("savePath"));
				mDLTask.dlThreadNum = localCursor.getInt(localCursor.getColumnIndex("dlThreadNum"));
				mDLTask.totalSize = localCursor.getLong(localCursor.getColumnIndex("totalSize"));
				mDLTask.fileName = localCursor.getString(localCursor.getColumnIndex("fileName"));
				mDLTask.createTime = localCursor.getLong(localCursor.getColumnIndex("createTime"));
				mDLTask.setErrorMessage(localCursor.getString(localCursor.getColumnIndex("errorMessage")));
			}
			localCursor.close();
			mDatabase.close();
			return mDLTask;
		}
	}

	/**
	 * 保存下载线程信息
	 * @param haseCode
	 * @param ttSet
	 */
	public void saveThTasks(int haseCode, Set<DLThreadTask> ttSet) {
		if (ttSet.size() == 0){
			return;
		}
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getWritableDatabase();
			mDatabase.beginTransaction();
			for(DLThreadTask dtTask : ttSet){
				dtTask.setIdCode();
				mDatabase.execSQL(
								"Insert Or Replace into DLThreadTask (idCode,taskHashCode,threadId,downloadBlock,hasDownloadLength,breakPointPosition,costTime) values (?,?,?,?,?,?,?);",
								new String[]{dtTask.idCode+"",dtTask.taskHashCode+"",
										dtTask.threadId+"",dtTask.downloadBlock+"",
										dtTask.hasDownloadLength+"",dtTask.breakPointPosition+""
										,dtTask.costTime+""});
			}
			mDatabase.setTransactionSuccessful();
			mDatabase.endTransaction();
			if (mDatabase.isOpen()){
				mDatabase.close();
			}
		}
	}

	/**
	 * 更新下载任务 
	 * @param mDLTask
	 */
	public void updateTask(DLTask mDLTask) {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getWritableDatabase();
			ContentValues cValues = new ContentValues();
			cValues.put("errorMessage", mDLTask.errorMessage);
			
			int i = mDatabase.update("DLTask", cValues,
					"downLoadUrl = ?", new String[]{mDLTask.downLoadUrl});
			ZWLogger.printLog(DLDatabaseHelper.class, "更新线程下载任务,线程id:" + i);
			if (mDatabase.isOpen()){
				mDatabase.close();
			}
			return;
		}
	}

	/**
	 * 更新下载任务 和下载线程
	 * @param mTask
	 * @param ttSet
	 */
	public void updateTasks(DLTask mTask, Set<DLThreadTask> ttSet) {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getWritableDatabase();
			mDatabase.beginTransaction();
			mDatabase
					.execSQL(
							"Insert Or Replace into DLTask (downLoadUrl,savePath,dlThreadNum,totalSize,fileName,createTime) values (?,?,?,?,?,?);",
							new String[]{mTask.downLoadUrl,mTask.savePath
									,mTask.dlThreadNum+"",mTask.totalSize+""
									,mTask.fileName,mTask.createTime+""});
			for(DLThreadTask dtt : ttSet){
				dtt.setIdCode();
				mDatabase
						.execSQL(
								"Insert Or Replace into DLThreadTask (idCode,taskHashCode,threadId,downloadBlock,hasDownloadLength,breakPointPosition,costTime) values (?,?,?,?,?,?,?);",
								new String[]{dtt.idCode+"",String.valueOf(dtt.taskHashCode)
										,String.valueOf(dtt.threadId),String.valueOf(dtt.downloadBlock)
										,String.valueOf(dtt.hasDownloadLength),String.valueOf(dtt.breakPointPosition)
										,dtt.costTime+""});
			}
			mDatabase.setTransactionSuccessful();
			mDatabase.endTransaction();
			if (mDatabase.isOpen()){
				mDatabase.close();
			} 
		}
	}

	public void updateThreadTask(DLTask task,DLThreadTask dtTask) {
		synchronized (LOCK) {
			dtTask.setIdCode();
			SQLiteDatabase mDatabase = getWritableDatabase();
			ContentValues cValues = new ContentValues();
			cValues.put("errorMessage", task.errorMessage);
			int num = mDatabase.update("DLTask", cValues,
					"downLoadUrl = ?", new String[]{task.downLoadUrl});
			ZWLogger.printLog(DLDatabaseHelper.class, "更新线程下载任务,影响条数:" + num);
			
			ContentValues cValues1 = new ContentValues();
			cValues1.put("downloadBlock", dtTask.downloadBlock);
			cValues1.put("hasDownloadLength", dtTask.hasDownloadLength);
			cValues1.put("breakPointPosition", dtTask.breakPointPosition);
			cValues1.put("costTime", dtTask.costTime);
			int tNum = mDatabase.update("DLThreadTask",
					cValues1, "idCode = "+ dtTask.idCode, null);
			ZWLogger.printLog(DLDatabaseHelper.class, "更新线程下载任务,影响条数:" + tNum);
			if (mDatabase.isOpen()){
				mDatabase.close();
			}
		}
	}

	/**
	 * 更新下载线程
	 * @param paramDLThreadTask
	 */
	public void updateThreadTask(DLThreadTask dtTask) {
		synchronized (LOCK) {
			SQLiteDatabase mDatabase = getWritableDatabase();
			dtTask.setIdCode();
			ContentValues cValues = new ContentValues();
			cValues.put("downloadBlock", dtTask.downloadBlock);
			cValues.put("hasDownloadLength", dtTask.hasDownloadLength);
			cValues.put("breakPointPosition", dtTask.breakPointPosition);
			cValues.put("costTime", dtTask.costTime);
			int num = mDatabase.update("DLThreadTask",
					cValues, "idCode = "+ dtTask.idCode, null);
			ZWLogger.printLog(DLDatabaseHelper.class, "更新线程下载任务,影响条数:" + num);
			if (mDatabase.isOpen()){
				mDatabase.close();
			}
		}
	}

	class DBOptTask extends AsyncTask<Void, Void, Void> {
		Runnable run;

		public DBOptTask(Runnable run) {
			this.run = run;
		}

		protected Void doInBackground(Void... paramArrayOfVoid) {
			if (this.run != null){
				run.run();
			}
			return null;
		}
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		DBUtil.createTable(db, DLTask.class, true);
		DBUtil.createTable(db, DLThreadTask.class, true);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		DBUtil.dropTable(db, DLTask.class);
		DBUtil.dropTable(db, DLThreadTask.class);
		onCreate(db);
	}
}
