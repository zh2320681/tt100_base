package cn.tt100.base.example.bean;

import cn.tt100.base.ZWBo;

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
