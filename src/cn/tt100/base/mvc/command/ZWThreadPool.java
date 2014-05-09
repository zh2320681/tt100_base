package cn.tt100.base.mvc.command;

import cn.tt100.base.util.ZWLogger;

/**
 * @Title ThreadPool
 * @package com.ta.mvc.command
 * @Description ThreadPool是command的线程池
 * @author 白猫
 * @date 2013-1-16 下午 16:51
 * @version V1.0
 */
public class ZWThreadPool {
	// 线程的最大数量
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
			ZWLogger.printLog(ZWThreadPool.this, "线程池开始运行！");
			int threadCount = MAX_THREADS_COUNT;

			threads = new ZWCommandThread[threadCount];
			for (int threadId = 0; threadId < threadCount; threadId++) {
				threads[threadId] = new ZWCommandThread(threadId);
				threads[threadId].start();
			}
			started = true;
			ZWLogger.printLog(ZWThreadPool.this, "线程池运行完成！");
		}
	}

	public void shutdown() {
		ZWLogger.printLog(ZWThreadPool.this, "关闭所有线程！");
		if (started) {
			for (ZWCommandThread thread : threads) {
				thread.stop();
			}
			threads = null;
			started = false;
		}
		ZWLogger.printLog(ZWThreadPool.this, "关闭完所有线程！");
	}
}
