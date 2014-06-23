package cn.shrek.base;

import java.util.Stack;

import cn.shrek.base.ui.ZWActivity;
import android.app.Activity;

public class ZWActivityManager<T extends Activity> {
	private Stack<T> activityStack;
	private static ZWActivityManager<? extends Activity> instance;

	private ZWActivityManager() {
		activityStack = new Stack<T>();
	}

	public static ZWActivityManager<? extends Activity> getInstance() {
		if (instance == null) {
			instance = new ZWActivityManager<Activity>();
		}
		return instance;
	}
	
	/**
	 * 关闭最上层 activity
	 */
	public void popActivity() {
		if(activityStack.size() > 0){
			Activity activity = activityStack.lastElement();
			if (activity != null) {
				activity.finish();
				activity = null;
			}
		}
		
	}

	
	/**
	 * 关闭 指定的 activity
	 * @param activity
	 */
	public void popActivity(Activity activity) {
		if(activity != null && !activity.isFinishing()){
			activity.finish();
		}
		
		if (activity != null) {
			activityStack.remove(activity);
			activity = null;
		}
		
	}

	/**
	 * 得到当前 activity
	 * @return
	 */
	public T currentActivity() throws NullPointerException{
		if(activityStack.size() > 0){
			T activity = activityStack.lastElement();
			return activity;
		}
//		throw new NullPointerException();
		return null;
	}

	
	/**
	 * 添加 新的 activity
	 * @param activity
	 */
	public void pushActivity(T activity) {
		if (activityStack == null) {
			activityStack = new Stack<T>();
		}
		activityStack.add(activity);
	}

	/*
	 * 关闭其他除了指定的
	 */
	public void popAllActivityExceptOne(Class<? extends Activity> cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}
	
	/*
	 * 关闭其他除了指定的
	 */
	public void popAllActivity() {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			popActivity(activity);
		}
	}
	
}