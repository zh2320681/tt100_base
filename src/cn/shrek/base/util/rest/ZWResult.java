package cn.shrek.base.util.rest;

import org.springframework.http.HttpStatus;

public class ZWResult<PARAEOBJ> {
	//������
	public HttpStatus requestCode;
	//������Ϣ
	public Exception errorException;
	
	public PARAEOBJ bodyObj;
	
}
