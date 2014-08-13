package cn.shrek.base.ui;

import java.lang.reflect.Constructor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import cn.shrek.base.ZWBo;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ZWLogger;

public class ZWHolderBo extends ZWBo {
	int position;
	// public Object data;

	View rootView;

	public ZWHolderBo() {
		// super();
	}

	public int getPosition() {
		return position;
	}

	public View getRootView() {
		return rootView;
	}

	/**
	 * 实例化 得到listHolder 注意 如果ZWHolderBo内部类 要填写 class对象 和 在谁的内部类里面hostObj
	 * 必须提供无参数的构造方法
	 * 
	 * @param ctx
	 * @param clazz
	 *            外部类 ZWHolderBo.class 内部类 xxx.xxx.ZWHolderBo.class
	 * @param hostObj
	 *            null 不是内部类 内部类 为外部类的示例
	 * @param layoutId
	 * @param parentView
	 * @param regex
	 * @return
	 */
	public static <HOLDER extends ZWHolderBo> HOLDER newInstance(Context ctx,
			Class<HOLDER> holderClazz, Object hostObj, int layoutId,
			View convertView, String regex) {
		HOLDER mHolder = null;
		if (convertView == null) {
			Constructor<HOLDER> constructor = null;
			try {
				constructor = holderClazz.getDeclaredConstructor(hostObj
						.getClass());
				constructor.setAccessible(true);
				mHolder = constructor.newInstance(hostObj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ZWLogger.e(ZWHolderBo.class, "Holder类 尝试不用内部类的方式");
				try {
					constructor = holderClazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					mHolder = constructor.newInstance();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					ZWLogger.e(ZWHolderBo.class, "Holder类 尝试外部类的构造方法，也找不到!");
				}
			}

			mHolder.initViewWeight(ctx, layoutId, convertView, regex);
		} else {
			mHolder = (HOLDER) convertView.getTag();
		}
		if (mHolder != null) {
			convertView = mHolder.rootView;
		}
		return mHolder;
	}

	public static <T extends ZWHolderBo> T newInstance(Context ctx,
			Class<T> clazz, int layoutId, View parentView, String regex) {
		return newInstance(ctx, clazz, null, layoutId, parentView, regex);
	}

	void initViewWeight(Context ctx, int layoutId, View parentView, String regex) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		rootView = inflater.inflate(layoutId, null);
		BaseUtil.initViews(ctx, rootView, this, regex);
		parentView = rootView;
		parentView.setTag(this);
	}
}
