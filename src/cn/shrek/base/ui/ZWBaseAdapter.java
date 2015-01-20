package cn.shrek.base.ui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.ui.inject.Injector;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.ZWLogger;

public abstract class ZWBaseAdapter<SOURCE, HOLDER extends ZWHolderBo> extends
		BaseAdapter {
	protected Context ctx;
	protected Collection<SOURCE> dataSource;
	Class<HOLDER> holderClazz;

	private int layoutId;
	private String regex;

	protected ViewGroup parent;

	protected LayoutInflater inflater;

	public ZWBaseAdapter(Context ctx, Class<HOLDER> holderClazz) {
		this(ctx, holderClazz, null);
	}

	public ZWBaseAdapter(Context ctx, Class<HOLDER> holderClazz,
			Collection<SOURCE> outDataSource) {
		super();
		this.ctx = ctx;

		// 注入器
		Injector.instance().injectValue(ctx, this);

		if (outDataSource == null) {
			dataSource = initData();
		} else {
			dataSource = outDataSource;
		}

		this.holderClazz = holderClazz;

		Class<?> clazz = getClass();
		Controller con = clazz.getAnnotation(Controller.class);
		if (con == null) {
			throw new ExceptionInInitializerError(
					"Adapter 请设置@Controller的layoutId和idFormat注解信息");
		}

		if (con.layoutId() == ZWConstants.NULL_INT_VALUE) {
			throw new ExceptionInInitializerError(
					"Adapter 请设置@Controller的layoutId信息");
		}

		if (con.idFormat() == ZWConstants.NULL_STR_VALUE) {
			throw new ExceptionInInitializerError(
					"Adapter 请设置@Controller的idFormat信息");
		}
		this.layoutId = con.layoutId();
		this.regex = con.idFormat();
	}

	/**
	 * 初始化数据
	 * 
	 * @return
	 */
	public Collection<SOURCE> initData() {
		return null;
	};

	public Collection<SOURCE> getDataSource() {
		return dataSource;
	}

	public void setDateSource(Collection<SOURCE> data) {
		dataSource = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (dataSource == null) {
			return 0;
		}
		return dataSource.size();
	}

	@Override
	public SOURCE getItem(int position) {
		// TODO Auto-generated method stub
		int i = 0;
		for (SOURCE s : dataSource) {
			if (i == position) {
				return s;
			}
			i++;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
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
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (parent != null) {
			this.parent = parent;
		}
		HOLDER mHolder = null;
		if (convertView == null) {
			Constructor<HOLDER> constructor = null;
			try {
				constructor = holderClazz.getDeclaredConstructor(getClass());
				constructor.setAccessible(true);
				mHolder = constructor.newInstance(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ZWLogger.e(this, "Holder类 尝试不用内部类的方式");
				try {
					constructor = holderClazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					mHolder = constructor.newInstance();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					ZWLogger.e(this, "Holder类 尝试外部类的构造方法，也找不到!");
				}
			}

			if (inflater == null) {
				inflater = LayoutInflater.from(ctx);
			}
			convertView = inflater.inflate(layoutId, null);
			mHolder.rootView = convertView;
			BaseUtil.initViews(ctx, convertView, mHolder, regex);
			convertView.setTag(mHolder);
		} else {
			mHolder = (HOLDER) convertView.getTag();
		}

		if(parent instanceof MyGridView){
			MyGridView mGridView = ((MyGridView)parent);
			if(!mGridView.isMeasure()){
				SOURCE source = getItem(position);

				mHolder.position = position;
				optView(mHolder, source, position);
			}
		}else{
			SOURCE source = getItem(position);

			mHolder.position = position;
			optView(mHolder, source, position);
		}

		return convertView;
	}

	/**
	 * <请不要放耗时操作>
	 * 
	 * @param tagHolder
	 * @param source
	 * @param position
	 */
	public abstract void optView(HOLDER tagHolder, SOURCE source, int position);

	/**
	 * 只刷新一个选项
	 * 
	 * @param position
	 */
	public void notifyDataSetChanged(int... positionArray) {
		// TODO Auto-generated method stub
		if (positionArray == null || positionArray.length == 0) {
//			throw new IllegalArgumentException("更新的数据源位置不合法!");
			ZWLogger.e(this, "更新的数据源位置为空!");
			return;
		}

		for (int position : positionArray) {
			if (position < 0) {
//				throw new IllegalArgumentException("更新的数据源位置不合法!");
				ZWLogger.e(this, "更新的数据源位置不合法!");
				continue;
			}
		}

		Arrays.sort(positionArray);

		List<Integer> ints = new ArrayList<Integer>();
		for (int value : positionArray) {
			ints.add(value);
		}

		int childNum = parent.getChildCount();
		for (int i = 0; i < childNum; i++) {
			if (ints.size() == 0) {
				return;
			}
			View child = parent.getChildAt(i);
			Object obj = child.getTag();
			if (obj != null && obj instanceof ZWHolderBo) {
				ZWHolderBo holder = (ZWHolderBo) obj;
				if (ints.contains(holder.position)) {
					ints.remove(Integer.valueOf(holder.position));
					getView(holder.position, child, parent);
				}
			}
		}
	}

	public void notifyDataSetChanged(SOURCE... sourceArray) {
		if (sourceArray == null || sourceArray.length == 0) {
			throw new IllegalArgumentException("更新的数据源位置不合法!");
		}

		for (SOURCE s : sourceArray) {
			if (s == null) {
				throw new IllegalArgumentException("更新的数据源位置不合法!");
			}
		}

		List<SOURCE> sourceList = Arrays.asList(sourceArray);
		List<Integer> ints = new ArrayList<Integer>();
		int i = 0;
		for (SOURCE s : dataSource) {
			if (sourceList.contains(s)) {
				ints.add(new Integer(i));
			}
			// if (source.equals(s)) {
			// s = source;
			// notifyDataSetChanged(i);
			// return;
			// }
			i++;
		}

		int[] notifyData = new int[ints.size()];
		for (int j = 0; j < ints.size(); j++) {
			notifyData[j] = ints.get(j);
		}
		notifyDataSetChanged(notifyData);
	}

	public void notifyDataSetChanged(CustomRule<SOURCE> customRule) {
		if(parent == null){
			for(SOURCE source : dataSource){
				customRule.ruleJudge(source);
			}
			notifyDataSetChanged();
			return;
		}
		int childNum = parent.getChildCount();
		for (int i = 0; i < childNum; i++) {
			View child = parent.getChildAt(i);
			Object obj = child.getTag();
			if (obj != null && obj instanceof ZWHolderBo) {
				ZWHolderBo holder = (ZWHolderBo) obj;
				SOURCE source = getItem(holder.position);
				if (customRule.ruleJudge(source)) {
					getView(holder.position, child, parent);
				}
			}
		}
	}

}
