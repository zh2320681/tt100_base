package cn.shrek.base.exception;

public class NetworkAvailableException extends Exception{

	/**
	 * 移动数据连接关闭
	 */
	private static final long serialVersionUID = -1240269706818302612L;

	public NetworkAvailableException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NetworkAvailableException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public NetworkAvailableException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public NetworkAvailableException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
