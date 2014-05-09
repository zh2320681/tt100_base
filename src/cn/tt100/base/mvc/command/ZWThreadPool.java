package cn.tt100.base.mvc.command;

import cn.tt100.base.util.ZWLogger;

/**
 * @Title ThreadPool
 * @package com.ta.mvc.command
 * @Description ThreadPool��command���̳߳�
 * @author ��è
 * @date 2013-1-16 ���� 16:51
 * @version V1.0
 */
public class ZWThreadPool {
	// �̵߳��������
	private static final int MAX_THREADS_COUNT = 2;
	private ZWCommandThread threads[] = null;
	private boolean started = false;
	private static ZWThreadPool instance;

	private ZWThreadPool() {

	}

	public static ZWThreadPool getInstance() {
		if (instance == null) {
			instance = new ZWThreadPool();
		}
		return instance;
	}

	public void start() {
		if (!started) {
			ZWLogger.printLog(ZWThreadPool.this, "�̳߳ؿ�ʼ���У�");
			int threadCount = MAX_THREADS_COUNT;

			threads = new ZWCommandThread[threadCount];
			for (int threadId = 0; threadId < threadCount; threadId++) {
				threads[threadId] = new ZWCommandThread(threadId);
				threads[threadId].start();
			}
			started = true;
			ZWLogger.printLog(ZWThreadPool.this, "�̳߳�������ɣ�");
		}
	}

	public void shutdown() {
		ZWLogger.printLog(ZWThreadPool.this, "�ر������̣߳�");
		if (started) {
			for (ZWCommandThread thread : threads) {
				thread.stop();
			}
			threads = null;
			started = false;
		}
		ZWLogger.printLog(ZWThreadPool.this, "�ر��������̣߳�");
	}
}
