package cn.tt100.base.util;

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
