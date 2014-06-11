package cn.shrek.base.mvc.command;

import java.util.concurrent.LinkedBlockingQueue;

import cn.shrek.base.util.ZWLogger;

/**
 * @Title ZWCommandQueue
 * @Description ZWCommandQueue维护一个Command
 * @author Shrek
 */
public class ZWCommandQueue {
	private LinkedBlockingQueue<ZWICommand> theQueue = new LinkedBlockingQueue<ZWICommand>();

	public ZWCommandQueue() {
		ZWLogger.printLog(ZWCommandQueue.this, "初始化Command队列");
	}

	public void enqueue(ZWICommand cmd) {
		ZWLogger.printLog(ZWCommandQueue.this, "添加Command到队列");
		theQueue.add(cmd);
	}

	public synchronized ZWICommand getNextCommand() {
		ZWLogger.printLog(ZWCommandQueue.this, "获取Command");
		ZWICommand cmd = null;
		try {
			ZWLogger.printLog(ZWCommandQueue.this, "CommandQueue::to-take");
			cmd = theQueue.take();
			ZWLogger.printLog(ZWCommandQueue.this, "CommandQueue::taken");
		} catch (InterruptedException e) {
			ZWLogger.printLog(ZWCommandQueue.this, "没有获取到Command");
			e.printStackTrace();
		}
		ZWLogger.printLog(ZWCommandQueue.this, "返回Command" + cmd);
		return cmd;
	}

	public synchronized void clear() {
		ZWLogger.printLog(ZWCommandQueue.this, "清空所有Command");
		theQueue.clear();
	}
}
