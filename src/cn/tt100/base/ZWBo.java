package cn.tt100.base;

import java.util.Date;

import cn.tt100.base.annotation.DatabaseField;

public class ZWBo {
	//����ʱ��
	@DatabaseField
	public Date createTime;
	//�Ƿ����
	@DatabaseField
	public boolean isExpired;
	
	public ZWBo(){
		super();
		createTime = new Date();
		isExpired = false;
	}

}
