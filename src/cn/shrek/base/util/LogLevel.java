package cn.shrek.base.util;

public enum LogLevel {
	DEBUG("����"),
	INFO("��Ϣ"),
	WARNING("����"),
	ERROR("����");
	
	public String descript;//����
	
	LogLevel(String descript){
		this.descript = descript;
	}
}
