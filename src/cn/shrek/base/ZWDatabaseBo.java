package cn.shrek.base;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import cn.shrek.base.annotation.DatabaseField;

public class ZWDatabaseBo extends ZWBo{
	
	// 创建时间
	@DatabaseField
	@JSONField(serialize=false)
	public Date createTime;
	
	// 是否过期
	@DatabaseField
	@JSONField(serialize=false)
	public boolean isExpired;

	public ZWDatabaseBo() {
		super();
		createTime = new Date();
		isExpired = false;
	}

}
