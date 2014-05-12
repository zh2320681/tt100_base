package cn.tt100.base.util.rest;

import org.springframework.http.HttpStatus;

public class ZWResult<PARAEOBJ> {
	//ÇëÇóÂë
	public HttpStatus requestCode;
	//´íÎóĞÅÏ¢
	public Exception errorException;
	
	public PARAEOBJ bodyObj;
	
}
