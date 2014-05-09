package cn.tt100.base.mvc.command;

import cn.tt100.base.util.ZWLogger;


/**
 * @Title TACommandThread
 * @package com.ta.mvc.command
 * @Description ZWCommandThread是一个专门执行command的线程
 */
public class ZWCommandThread implements Runnable {
	private int threadId;
	private Thread thread = null;
	private boolean running = false;
	private boolean stop = false;

	public ZWCommandThread(int threadId) {
		ZWLogger.printLog(ZWCommandThread.this, "CommandThread::ctor");
		this.threadId = threadId;
		thread = new Thread(this);
	}

	public void run() {
		ZWLogger.printLog(ZWCommandThread.this, "CommandThread::run-enter");
		while (!stop) {
			ZWLogger.printLog(ZWCommandThread.this, "CommandThread::get-next-command");
			ZWICommand cmd = ZWCommandQueueManager.getInstance()
					.getNextCommand();
			ZWLogger.printLog(ZWCommandThread.this, "CommandThread::to-execute");
			cmd.execute();
			ZWLogger.printLog(ZWCommandThread.this, "CommandThread::executed");
		}
		ZWLogger.printLog(ZWCommandThread.this, "CommandThread::run-exit");
	}

	public void start() {
		thread.start();
		running = true;
	}

	public void stop() {
		stop = true;
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public int getThreadId() {
		return threadId;
	}
}
