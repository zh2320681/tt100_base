package cn.shrek.base.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.ui.inject.Injector;
import cn.shrek.base.util.ZWLogger;

public abstract class ZWFragment extends Fragment {
//	private int layoutId;
	
	public View rootView;
	
//	public ZWFragment(int layoutId){
//		super();
//		this.layoutId = layoutId;
//	}

	protected void onPreCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		onPreCreateView(inflater,container,savedInstanceState);
		int layoutId = 0;
		Class<?> clazz = getClass();
		Controller selector = clazz.getAnnotation(Controller.class);
		try {
			if (selector != null) {
				layoutId = selector.layoutId();
			} else {
				layoutId = getResources().getIdentifier(
						clazz.getSimpleName().toLowerCase().replace("fragment", ""),
						"layout", getActivity().getPackageName());
			}
		} catch (Exception e) {
			// 设置布局失败
			ZWLogger.printLog(this, "Fragment名称:" + clazz.getSimpleName()
					+ "加载布局失败!");
			e.printStackTrace();
		}
		
		if(layoutId == 0){
			ZWLogger.printLog(this, "Fragment名称:" + clazz.getSimpleName()
					+ "加载布局失败!");
			throw new InstantiationError("构建Fragment布局失败！");
		}
		rootView = inflater.inflate(layoutId, container, false);
		Injector.instance().injectValue(getActivity(), this);
		onCreateView(savedInstanceState);
		return rootView;
	}
	
	public abstract void onCreateView(Bundle savedInstanceState);
	
	/**
	 * 得到tag的子串
	 * @return
	 */
	public String getTagInfo(){
		return getClass().getSimpleName();
	}
	
}
