package cn.shrek.base.download;

public class DLConstant {

	/**
	 * �㲥 �Լ��㲥������
	 */
	public static final String BROADCAST_TASK = "BROADCAST_TASK";
	public static final String DL_TASK_FLAG = "f";
	public static final String DL_TASK_MSG = "e";
	public static final String DL_TASK_OBJ = "d";
	
	/**
	 * �����̳߳��ֳ�ͻ CONFLICT_DEFAULT �����Ϊ�� CONFLICT_LAST ���ϴ�Ϊ��  CONFLICT_RETURN ����
	 */
	public static final int CONFLICT_DEFAULT = 0x17;
	public static final int CONFLICT_LAST = 0x18;
	public static final int CONFLICT_RETURN = 0x19;
	
	/**
	 * ���������ʱ�� ��ô���� ERROR_DEFAULT���� ERROR_AGAIN_ONCE ��������һ��  ERROR_AGAIN_HEARTBEAT ��������
	 */
	public static final int ERROR_AGAIN_HEARTBEAT = 0x35;
	public static final int ERROR_AGAIN_ONCE = 0x34;
	public static final int ERROR_DEFAULT = 0x33;
	
	//��Ч��������Ŀ
	public static final int NAN_THREAD_NUM = 1;
	
	/**
	 * ��������״̬
	 */
	public static final int TASK_ERROR = 4;
	public static final int TASK_PAUSE = 3;
	public static final int TASK_RUN = 2;
	public static final int TASK_SUCESS = 5;
	public static final int TASK_WAIT = 1;

}
