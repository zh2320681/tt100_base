package cn.shrek.base.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import cn.shrek.base.ZWBo;
import cn.shrek.base.example.ListTestActivity;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ZWLogger;

public class ZWHolderBo extends ZWBo {
	View rootView;
	
	public ZWHolderBo() {
//		super();
	}

	// public void initViewWeight(Context ctx){
	// initViewWeight(ctx, null, "?");
	// }

	// public void initViewWeight(Context ctx,String regex){
	// initViewWeight(ctx, null, regex);
	// }

	void initViewWeight(Context ctx, int layoutId ,View parentView, String regex) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		rootView = inflater.inflate(layoutId, null);
		BaseUtil.initViews(ctx, rootView, this, regex);
		parentView = rootView;
		parentView.setTag(this);
	}

	public View getRootView() {
		if (rootView == null) {
			new Exception(
					"RootView is null, please invoke \'initViewWeight()\' method");
		}
		return rootView;
	}

	/**
	 * 实例化 得到listHolder
	 * 注意 如果ZWHolderBo内部类  要填写 class对象 和 在谁的内部类里面hostObj
	 * 必须提供无参数的构造方法
	 * @param ctx
	 * @param clazz 外部类 ZWHolderBo.class  内部类 xxx.xxx.ZWHolderBo.class
	 * @param hostObj  null 不是内部类   内部类 为外部类的示例
	 * @param layoutId
	 * @param parentView
	 * @param regex
	 * @return
	 */
	public static <T extends ZWHolderBo> T newInstance(Context ctx,
			Class<T> clazz, Object hostObj,int layoutId, View parentView, String regex) {
		T t = null;
		try {
			if(parentView == null){
				Constructor<T> constructor = null;
				if(hostObj == null){
					constructor = clazz.getDeclaredConstructor();
				}else{
					constructor = clazz.getDeclaredConstructor(hostObj.getClass());
				}
				constructor.setAccessible(true);
				if(hostObj == null){
					t = constructor.newInstance();
				}else{
					t = constructor.newInstance(hostObj);
				}
//				t.layoutId = layoutId;
				t.initViewWeight(ctx, layoutId,parentView ,regex);
			}else{
				t = (T)parentView.getTag();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ZWLogger.printLog(clazz.getSimpleName(), "ZWHolderBo的子类 必须有子类的构造方法");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
	
	public static <T extends ZWHolderBo> T newInstance(Context ctx,
			Class<T> clazz,int layoutId, View parentView, String regex) {
		return newInstance(ctx,clazz,null,layoutId,parentView,regex);
	}
}
