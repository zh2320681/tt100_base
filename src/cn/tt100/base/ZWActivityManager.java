package cn.tt100.base;

import java.util.Stack;

import android.app.Activity;

public class ZWActivityManager {
	private Stack<ZWActivity> activityStack;
	private static ZWActivityManager instance;

	private ZWActivityManager() {
		activityStack = new Stack<ZWActivity>();
	}

	public static ZWActivityManager getInstance() {
		if (instance == null) {
			instance = new ZWActivityManager();
		}
		return instance;
	}
	
	/**
	 * �ر����ϲ� activity
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
	 * �ر� ָ���� activity
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
	 * �õ���ǰ activity
	 * @return
	 */
	public ZWActivity currentActivity() throws NullPointerException{
		if(activityStack.size() > 0){
			ZWActivity activity = activityStack.lastElement();
			return activity;
		}
//		throw new NullPointerException();
		return null;
	}

	
	/**
	 * ��� �µ� activity
	 * @param activity
	 */
	public void pushActivity(ZWActivity activity) {
		if (activityStack == null) {
			activityStack = new Stack<ZWActivity>();
		}
		activityStack.add(activity);
	}

	/*
	 * �ر���������ָ����
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
	 * �ر���������ָ����
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