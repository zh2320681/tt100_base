package cn.shrek.base.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cn.shrek.base.ModelObservable;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.exception.ZWAppException;
import cn.shrek.base.ui.inject.Injector;
import cn.shrek.base.ui.inject.rule.KeyboardFocus;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ZWLogger;
import cn.shrek.base.util.net.ZWNetChangeObserver;
import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;
import cn.shrek.base.util.net.ZWNetworkStateReceiver;
import cn.shrek.base.util.rest.ZWAsyncTask;

public abstract class ZWActivity extends Activity implements Observer,
		ZWNetChangeObserver, KeyboardFocus {
	// private LinkedList<WeakReference<ZWAsyncTask<?>>> taskList;
	private Collection<ZWAsyncTask<?>> taskList;

	private Set<View> keyboardFocusViews;

	private static String packageName;
	private String activityName;

	protected Controller mController;

	// @AutoInject
	// public Identity mIdentity;

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
		mController = clazz.getAnnotation(Controller.class);
		try {
			if (mController != null
					&& mController.layoutId() != ZWConstants.NULL_INT_VALUE) {
				setContentView(mController.layoutId());
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
		if (!ZWApplication.isInterceptSameRequest) {
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (mController != null && mController.isMonitorNetwork()) {
			ZWNetworkStateReceiver.registerNetworkStateReceiver(this);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mController != null && mController.isMonitorNetwork()) {
			ZWNetworkStateReceiver.unRegisterNetworkStateReceiver(this);
		}
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

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && v instanceof EditText) {
			View[] views;
			if(keyboardFocusViews != null){
				views = new View[keyboardFocusViews.size()+1];
				views[0] = v;
				
				int temp = 1;
				for(View focusView : keyboardFocusViews){
					views[temp] =  focusView;
					temp++;
				}
			} else {
				views = new View[1];
				views[0] = v;
			}
			
			return !BaseUtil.isViewInArea(event.getX(), event.getY(), views);
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
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

	@Override
	public void addKeyboardFocus(View view) {
		// TODO Auto-generated method stub
		if (keyboardFocusViews == null) {
			keyboardFocusViews = new HashSet<View>();
		}
		keyboardFocusViews.add(view);
	}

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
