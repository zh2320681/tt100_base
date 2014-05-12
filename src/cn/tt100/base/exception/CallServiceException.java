package cn.tt100.base.exception;

import org.apache.http.HttpStatus;

public class CallServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  �������� ʧ��
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
	 * ���췽��
	 * @param code  ���������
	 */
	public CallServiceException(int code){
		super(parseCode(code));
	}
	
	
	private static String parseCode(int code){
		String exceptionStr = "";
		switch(code){
			case HttpStatus.SC_BAD_REQUEST:
				exceptionStr = "�����������������﷨!";
				break;
			case HttpStatus.SC_BAD_GATEWAY:
				exceptionStr = "��������Ϊ���ػ�������!";
				break;
			case HttpStatus.SC_CONFLICT:
				exceptionStr = "���������������ʱ������ͻ�� ��������������Ӧ�а����йس�ͻ����Ϣ!";
				break;
			case HttpStatus.SC_EXPECTATION_FAILED:
				exceptionStr = "������δ�������������ͷ�ֶε�Ҫ��!";
				break;
			case 500:
				exceptionStr = "��������������쳣!";
				break;
			case 401:
				exceptionStr = "�û�������������֤ʧ��!";
				break;
			case 404:
				exceptionStr = "��ѯ����δ�ҵ�!";
				break;
			case 302:
				exceptionStr = "�����������ַ����ȷ!";
				break;	
				
			default:
				exceptionStr = "�������˳��ִ���!";
				break;
		}
		return exceptionStr;
	}
}
