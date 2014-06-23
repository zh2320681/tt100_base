package cn.shrek.base.util;

public enum LogLevel {
	DEBUG("调试"),
	INFO("信息"),
	WARNING("警告"),
	ERROR("错误");
	
	public String descript;//描述
	
	LogLevel(String descript){
		this.descript = descript;
	}
}
