package cn.shrek.base.example.bean;

import cn.shrek.base.ZWBo;

public class MineBo extends ZWBo {
	Weatherinfo weatherinfo;

	public MineBo(){
		super();
	}
	
	@Override
	public String toString() {
		return "MineBo [weatherinfo=" + weatherinfo + "]";
	}
	
	
}
