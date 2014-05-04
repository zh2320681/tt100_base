package cn.tt100.base;

import java.util.HashMap;

import cn.tt100.base.util.ZWLogger;
import android.view.View;

public class ObserverContainer {
	public final static HashMap<String, ModelObservable> container = new HashMap<String, ModelObservable>();
	
	public static void addObservable(String hashCodeKey , ModelObservable mModelObservable){
//		if(!container.containsKey(hashCodeKey)){
//			container.put(hashCodeKey, mModelObservable);
//		}
		//存在key 也直接替换
		container.put(hashCodeKey, mModelObservable);
	}
	
	public static void addObserver(String hashCodeKey ,ViewObserver<? extends View> mViewObserver){
		if(container.containsKey(hashCodeKey)){
			container.get(hashCodeKey).addObserver(mViewObserver);
		}else{
			ZWLogger.printLog(ObserverContainer.class, "未找到观察对象,无法添加观察者");
		}
	}
	
	public static void notifyObserver(String hashCodeKey,Object newData){
		if(container.containsKey(hashCodeKey)){
			container.get(hashCodeKey).changeData(newData);
		}
	}
	
}
