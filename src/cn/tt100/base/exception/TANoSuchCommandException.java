package cn.tt100.base.exception;

/**
 * @Title ZWNoSuchCommandException
 * @Description ZWNoSuchCommandException是当没有找到相应资源ID名字的资源时，抛出此异常！
 */
public class TANoSuchCommandException extends Exception {
	private static final long serialVersionUID = 1L;

	public TANoSuchCommandException() {
		super();
	}

	public TANoSuchCommandException(String detailMessage) {
		super(detailMessage);

	}

}
