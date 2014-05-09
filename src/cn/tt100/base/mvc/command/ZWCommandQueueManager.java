package cn.tt100.base.mvc.command;

import cn.tt100.base.util.ZWLogger;


/**
 * @Description ZWCommandQueueManager��command���еĹ�����
 */
public final class ZWCommandQueueManager {
	private static ZWCommandQueueManager instance;
	private boolean initialized = false;
	private ZWThreadPool pool;
	private ZWCommandQueue queue;

	private ZWCommandQueueManager() {
	}

	public static ZWCommandQueueManager getInstance() {
		if (instance == null) {
			instance = new ZWCommandQueueManager();
		}
		return instance;
	}

	public void initialize() {
		ZWLogger.printLog(ZWCommandQueueManager.this, "׼����ʼ����");
		if (!initialized) {
			ZWLogger.printLog(ZWCommandQueueManager.this, "���ڳ�ʼ����");
			queue = new ZWCommandQueue();
			pool = ZWThreadPool.getInstance();
			ZWLogger.printLog(ZWCommandQueueManager.this, "��ɳ�ʼ����");

			pool.start();
			initialized = true;
		}
		ZWLogger.printLog(ZWCommandQueueManager.this, "��ʼ����ɣ�");
	}

	/**
	 * �Ӷ����л�ȡCommand
	 * 
	 * @return ZWICommand
	 */
	public ZWICommand getNextCommand() {
		ZWLogger.printLog(ZWCommandQueueManager.this, "��ȡCommand��");
		ZWICommand cmd = queue.getNextCommand();
		ZWLogger.printLog(ZWCommandQueueManager.this, "��ȡCommand" + cmd + "��ɣ�");
		return cmd;
	}

	/**
	 * ���Command��������
	 */
	public void enqueue(ZWICommand cmd) {
		ZWLogger.printLog(ZWCommandQueueManager.this, "���" + cmd + "��ʼ");
		queue.enqueue(cmd);
		ZWLogger.printLog(ZWCommandQueueManager.this, "���" + cmd + "���");
	}

	/**
	 * �������
	 */
	public void clear() {
		queue.clear();
	}

	/**
	 * �رն���
	 */
	public void shutdown() {
		if (initialized) {
			queue.clear();
			pool.shutdown();
			initialized = false;
		}
	}
}
