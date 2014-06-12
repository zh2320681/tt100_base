package cn.shrek.base.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import cn.shrek.base.ZWBo;
import cn.shrek.base.util.BaseUtil;

public class ZWHolderBo extends ZWBo {
	int layoutId;
	
	public ZWHolderBo(int layoutId){
		this.layoutId = layoutId;
	}
	
	
	public void initViewWeight(Context ctx){
		initViewWeight(ctx, null, "?");
	}
	
	public void initViewWeight(Context ctx,String regex){
		initViewWeight(ctx, null, regex);
	}
	
	public void initViewWeight(Context ctx,View parentView,String regex){
		LayoutInflater inflater = LayoutInflater.from(ctx);
		inflater.inflate(layoutId, null);
		BaseUtil.initViews(ctx, parentView, this, regex);
	}
}
