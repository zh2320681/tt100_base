package cn.shrek.base.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.shrek.base.annotation.LayoutSelector;
import cn.shrek.base.ui.inject.Injector;
import cn.shrek.base.util.ZWLogger;

public abstract class ZWFragment extends Fragment {
//	private int layoutId;
	
	public View rootView;
	
//	public ZWFragment(int layoutId){
//		super();
//		this.layoutId = layoutId;
//	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		int layoutId = 0;
		Class<?> clazz = getClass();
		LayoutSelector selector = clazz.getAnnotation(LayoutSelector.class);
		try {
			if (selector != null) {
				layoutId = selector.id();
			} else {
				layoutId = getResources().getIdentifier(
						clazz.getSimpleName().toLowerCase().replace("fragment", ""),
						"layout", getActivity().getPackageName());
			}
		} catch (Exception e) {
			// ���ò���ʧ��
			ZWLogger.printLog(this, "Fragment����:" + clazz.getSimpleName()
					+ "���ز���ʧ��!");
			e.printStackTrace();
		}
		
		if(layoutId == 0){
			ZWLogger.printLog(this, "Fragment����:" + clazz.getSimpleName()
					+ "���ز���ʧ��!");
			throw new InstantiationError("����Fragment����ʧ�ܣ�");
		}
		rootView = inflater.inflate(layoutId, container, false);
		Injector.instance().injectValue(getActivity(), this);
		onCreateView(savedInstanceState);
		return rootView;
	}
	
	public abstract void onCreateView(Bundle savedInstanceState);
	
	/**
	 * �õ�tag���Ӵ�
	 * @return
	 */
	public String getTagInfo(){
		return getClass().getSimpleName();
	}
	
}
