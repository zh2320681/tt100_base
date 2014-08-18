package cn.shrek.base;

import java.util.Date;

import cn.shrek.base.annotation.DatabaseField;

public class ZWDatabaseBo extends ZWBo{
	// 创建时间
	@DatabaseField
	public Date createTime;
	// 是否过期
	@DatabaseField
	public boolean isExpired;

	public ZWDatabaseBo() {
		super();
		createTime = new Date();
		isExpired = false;
	}

}
