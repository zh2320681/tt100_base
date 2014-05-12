package cn.tt100.base.exception;

import org.apache.http.HttpStatus;

public class CallServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  请求服务端 失败
	 */

//	public CallServiceException() {
//		
//		super();
//	}
//
//	private CallServiceException(String detailMessage, Throwable throwable) {
//		super(detailMessage, throwable);
//	}
//
//	private CallServiceException(String detailMessage) {
//		super(detailMessage);
//	}
//
//	private CallServiceException(Throwable throwable) {
//		super(throwable);
//	}
	 
	/**
	 * 构造方法
	 * @param code  请求错误码
	 */
	public CallServiceException(int code){
		super(parseCode(code));
	}
	
	
	private static String parseCode(int code){
		String exceptionStr = "";
		switch(code){
			case HttpStatus.SC_BAD_REQUEST:
				exceptionStr = "服务器不理解请求的语法!";
				break;
			case HttpStatus.SC_BAD_GATEWAY:
				exceptionStr = "服务器作为网关或代理错误!";
				break;
			case HttpStatus.SC_CONFLICT:
				exceptionStr = "服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息!";
				break;
			case HttpStatus.SC_EXPECTATION_FAILED:
				exceptionStr = "服务器未满足期望请求标头字段的要求!";
				break;
			case 500:
				exceptionStr = "请求服务器出现异常!";
				break;
			case 401:
				exceptionStr = "用户名或者密码验证失败!";
				break;
			case 404:
				exceptionStr = "查询数据未找到!";
				break;
			case 302:
				exceptionStr = "请求服务器地址不正确!";
				break;	
				
			default:
				exceptionStr = "请求服务端出现错误!";
				break;
		}
		return exceptionStr;
	}
}
