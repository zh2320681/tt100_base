package cn.tt100.base.exception;

/**
 * @Title ZWNoSuchCommandException
 * @Description ZWNoSuchCommandException�ǵ�û���ҵ���Ӧ��ԴID���ֵ���Դʱ���׳����쳣��
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
