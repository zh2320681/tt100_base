package cn.tt100.base;

import java.util.Date;

import cn.tt100.base.annotation.DatabaseField;
import cn.tt100.base.ormlite.dao.DBTransforFactory;

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
