package cn.shrek.base.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import cn.shrek.base.ModelObservable;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.annotation.LayoutSelector;
import cn.shrek.base.exception.ZWAppException;
import cn.shrek.base.ui.inject.Injector;
import cn.shrek.base.util.ZWLogger;
import cn.shrek.base.util.net.ZWNetChangeObserver;
import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;
import cn.shrek.base.util.rest.ZWAsyncTask;

public abstract class ZWActivity extends Activity implements Observer,ZWNetChangeObserver {
	// private LinkedList<WeakReference<ZWAsyncTask<?>>> taskList;
	private Collection<ZWAsyncTask<?>> taskList;

	private static String packageName;
	private String activityName;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ZWAppException.getInstance(this);
		if (packageName == null) {
			packageName = getPackageName();
		}

		activityName = getClass().getSimpleName();
		Application app = getApplication();
		if (app instanceof ZWApplication) {
			ZWApplication zwApp = (ZWApplication) app;
			zwApp.mActivityManager.pushActivity(this);
		}

		if (ZWApplication.isLoadRestRequest) {
			taskList = ZWApplication.isInterceptSameRequest ? new HashSet<ZWAsyncTask<?>>()
					: new LinkedList<ZWAsyncTask<?>>();
		}

		onBaseCreate(savedInstanceState);
		Injector.instance().injectValue(this, this);

		initialize();
		addListener();

	}

	protected void onBaseCreate(Bundle savedInstanceState) {
		Class<? extends Activity> clazz = getClass();
		LayoutSelector selector = clazz.getAnnotation(LayoutSelector.class);
		try {
			if (selector != null) {
				setContentView(selector.id());
			} else {
				setContentView(getResources().getIdentifier(
						activityName.toLowerCase().replace("activity", ""),
						"layout", packageName));
			}
		} catch (Exception e) {
			// 设置布局失败
			ZWLogger.printLog(activityName, "Activity名称:" + activityName
					+ "加载布局失败!");
			e.printStackTrace();
		}
	}

	/**
	 * 初始化
	 */
	protected abstract void initialize();

	/**
	 * 添加监听器
	 */
	protected abstract void addListener();


	private int getIdValueIntoR(String idName) {
		// Class clazz = R.id.class;
		// int value = -1;
		// try {
		// Field f = clazz.getField(idName);
		// value = f.getInt(null);
		// } catch (NoSuchFieldException e) {
		// System.out.println("未找到id:["+idName+"]的控件");
		// } catch (Exception e) {
		// System.out.println("未找到id:["+idName+"]的控件");
		// }
		// return value;
		return getResources().getIdentifier(idName, "id", packageName);
	}

	@Override
	public final void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if (observable instanceof ModelObservable) {
			ModelObservable mModelObservable = (ModelObservable) observable;
			notifyObserver(mModelObservable.getData(), data);
			mModelObservable.setData(data);
		}

	}

	/**
	 * 添加任务
	 * 
	 * @param task
	 */
	public boolean addTask(ZWAsyncTask<?> task) {
		if(!ZWApplication.isInterceptSameRequest){
			taskList.add(task);
			return true;
		}
		
		if (ZWApplication.isLoadRestRequest) {
			boolean isExist = false;
			for (ZWAsyncTask<?> zwTask : taskList) {
				if (zwTask.equals(task)) {
					isExist = true;
					break;
				}
			}
			if (isExist) {
				ZWLogger.printLog(this, "任务:" + task.getTaskGuid()
						+ "已经存在了,不能再执行!");
			} else {
				taskList.add(task);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Application app = getApplication();
		if (app instanceof ZWApplication) {
			ZWApplication zwApp = (ZWApplication) app;
			zwApp.mActivityManager.popActivity(this);
		}
		clearTaskList();
	}

	public void clearTaskList() {
		for (ZWAsyncTask<?> task : taskList) {
			if (task != null) {
				task.cancel(true);
			}
		}
		taskList.clear();
	}

	/**
	 * 移除任务
	 * 
	 * @param task
	 */
	public void removeTask(ZWAsyncTask<?> task) {
		if (ZWApplication.isLoadRestRequest) {
			taskList.remove(task);
		}
	}

	public abstract void notifyObserver(Object oldObj, Object newObj);

	/**
	 * 网络连接连接时调用
	 */
	public void onConnect(NetType type) {

	}

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect() {

	}
}
