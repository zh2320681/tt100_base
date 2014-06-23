package cn.shrek.base.util.rest;

import org.springframework.http.HttpStatus;

public class ZWResult<PARAEOBJ> {
	//请求码
	public HttpStatus requestCode;
	//错误信息
	public Exception errorException;
	
	public PARAEOBJ bodyObj;
	
}
