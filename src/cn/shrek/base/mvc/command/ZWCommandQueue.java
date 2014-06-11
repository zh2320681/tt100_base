package cn.shrek.base.mvc.command;

import java.util.concurrent.LinkedBlockingQueue;

import cn.shrek.base.util.ZWLogger;

/**
 * @Title ZWCommandQueue
 * @Description ZWCommandQueueά��һ��Command
 * @author Shrek
 */
public class ZWCommandQueue {
	private LinkedBlockingQueue<ZWICommand> theQueue = new LinkedBlockingQueue<ZWICommand>();

	public ZWCommandQueue() {
		ZWLogger.printLog(ZWCommandQueue.this, "��ʼ��Command����");
	}

	public void enqueue(ZWICommand cmd) {
		ZWLogger.printLog(ZWCommandQueue.this, "���Command������");
		theQueue.add(cmd);
	}

	public synchronized ZWICommand getNextCommand() {
		ZWLogger.printLog(ZWCommandQueue.this, "��ȡCommand");
		ZWICommand cmd = null;
		try {
			ZWLogger.printLog(ZWCommandQueue.this, "CommandQueue::to-take");
			cmd = theQueue.take();
			ZWLogger.printLog(ZWCommandQueue.this, "CommandQueue::taken");
		} catch (InterruptedException e) {
			ZWLogger.printLog(ZWCommandQueue.this, "û�л�ȡ��Command");
			e.printStackTrace();
		}
		ZWLogger.printLog(ZWCommandQueue.this, "����Command" + cmd);
		return cmd;
	}

	public synchronized void clear() {
		ZWLogger.printLog(ZWCommandQueue.this, "�������Command");
		theQueue.clear();
	}
}
