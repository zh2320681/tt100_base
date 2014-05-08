package cn.tt100.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View.OnClickListener;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;
import cn.tt100.base.annotation.OberverLoad;
import cn.tt100.base.exception.ZWAppException;
import cn.tt100.base.util.BaseUtil;
import cn.tt100.base.util.ZWLogger;
import cn.tt100.base.util.net.ZWNetWorkUtil.NetType;

public abstract class ZWActivity extends Activity implements Observer{
	private static String packageName;
	private String activityName;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		ZWAppException.getInstance(this);
		if(packageName == null){
			packageName = getPackageName();
		}
		
		activityName = getClass().getSimpleName();
		Application app = getApplication();
		if(app instanceof ZWApplication){
			ZWApplication zwApp = (ZWApplication)app;
			zwApp.mActivityManager.pushActivity(this);
		}
		onBaseCreate(savedInstanceState);
		loadField();
		
		initialize();
		addListener();
		
	}
	
	
	protected void onBaseCreate(Bundle savedInstanceState){
		setContentView(getResources().getIdentifier(activityName.toLowerCase().replace("activity", ""), "layout", packageName));
	}
	
	
	/**
	 * 初始化
	 */
	protected abstract void initialize();
	/**
	 * 添加监听器
	 */
	protected abstract void addListener();
	
	private final void loadField(){
//		Class<? extends View> viewClazz = Class.forName("android.view.View");
		
		Class<? extends Activity> clazz = getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field f : fields){
			//自动初始化
			AutoInitialize autoInitialize = f.getAnnotation(AutoInitialize.class);
			if(autoInitialize != null){
				String idFormat = autoInitialize.idFormat().replace("?", f.getName()); //main_textBtn
				int value = getIdValueIntoR(idFormat);
				f.setAccessible(true);
				try {
					f.set(this, findViewById(value));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					ZWLogger.printLog(this, f.getName()+"赋值失败!");
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					ZWLogger.printLog(this, f.getName()+"赋值时访问失败!");
				}
			}
			
			//自动设置OnClcikListener
			AutoOnClick autoOnClick = f.getAnnotation(AutoOnClick.class);
			if(autoOnClick != null){
				Class<?> viewClazz = f.getType();
//				Object subView = f.get(this);
				Method clickMethod = null;
				try {
					clickMethod = viewClazz.getMethod("setOnClickListener", OnClickListener.class);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					ZWLogger.printLog(this, f.getName()+"设置setOnClickListener()方法失败!");
				}
				if(clickMethod != null){
					Field clickField;
					try {
						clickField = clazz.getDeclaredField(autoOnClick.clickSelector());
						clickField.setAccessible(true);
						clickMethod.invoke(f.get(this), clickField.get(this));
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(this, "没有找到方法:"+autoOnClick.clickSelector());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(BaseUtil.class, f.getName()+"赋值失败!");
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						ZWLogger.printLog(BaseUtil.class, f.getName()+"赋值时访问失败!");
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			
			//自动设置观察绑定
			OberverLoad oberverLoad = f.getAnnotation(OberverLoad.class);
			if(oberverLoad != null){
				f.setAccessible(true);
				Object obj;
				try {
					obj = f.get(this);
					ModelObservable mModelObservable = new ModelObservable(obj);
					mModelObservable.addObserver(this);
					ObserverContainer.addObservable(f.getName(), mModelObservable);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					ZWLogger.printLog(BaseUtil.class, f.getName()+"赋值失败!");
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					ZWLogger.printLog(BaseUtil.class, f.getName()+"赋值时访问失败!");
				}
				
			}
			
		}
	}
	
	
	private int getIdValueIntoR(String idName){
//		Class clazz = R.id.class;
//		int value = -1;
//		try {
//			Field f = clazz.getField(idName);
//			value = f.getInt(null);
//		} catch (NoSuchFieldException e) {
//			System.out.println("未找到id:["+idName+"]的控件");
//		} catch (Exception e) {
//			System.out.println("未找到id:["+idName+"]的控件");
//		}
//		return value;
		return getResources().getIdentifier(idName, "id", packageName);
	}
	
	
	@Override
	public final void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if(observable instanceof ModelObservable){
			ModelObservable mModelObservable = (ModelObservable) observable;
			notifyObserver(mModelObservable.getData(), data);
			mModelObservable.setData(data);
		}
		
	}
	
	public abstract void notifyObserver(Object oldObj , Object newObj);
	
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
