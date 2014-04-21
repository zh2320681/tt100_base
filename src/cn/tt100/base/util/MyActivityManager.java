package cn.tt100.base.util;

import java.util.Stack;

import android.app.Activity;

public class MyActivityManager {
	private Stack<Activity> activityStack;
	private static MyActivityManager instance;

	private MyActivityManager() {
		activityStack = new Stack<Activity>();
	}

	public static MyActivityManager getInstance() {
		if (instance == null) {
			instance = new MyActivityManager();
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
	public Activity currentActivity() {
		if(activityStack.size() > 0){
			Activity activity = activityStack.lastElement();
			return activity;
		}
		return null;
	}

	
	/**
	 * 添加 新的 activity
	 * @param activity
	 */
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
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