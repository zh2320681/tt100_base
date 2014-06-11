package cn.shrek.base;

import java.util.Date;

import cn.shrek.base.annotation.DatabaseField;

public class ZWBo {
	//创建时间
	@DatabaseField
	public Date createTime;
	//是否过期
	@DatabaseField
	public boolean isExpired;
	
	public ZWBo(){
		super();
		createTime = new Date();
		isExpired = false;
	}

}
