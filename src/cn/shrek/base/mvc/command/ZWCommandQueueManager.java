package cn.shrek.base.mvc.command;

import cn.shrek.base.util.ZWLogger;


/**
 * @Description ZWCommandQueueManager是command队列的管理者
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
		ZWLogger.printLog(ZWCommandQueueManager.this, "准备初始化！");
		if (!initialized) {
			ZWLogger.printLog(ZWCommandQueueManager.this, "正在初始化！");
			queue = new ZWCommandQueue();
			pool = ZWThreadPool.getInstance();
			ZWLogger.printLog(ZWCommandQueueManager.this, "完成初始化！");

			pool.start();
			initialized = true;
		}
		ZWLogger.printLog(ZWCommandQueueManager.this, "初始化完成！");
	}

	/**
	 * 从队列中获取Command
	 * 
	 * @return ZWICommand
	 */
	public ZWICommand getNextCommand() {
		ZWLogger.printLog(ZWCommandQueueManager.this, "获取Command！");
		ZWICommand cmd = queue.getNextCommand();
		ZWLogger.printLog(ZWCommandQueueManager.this, "获取Command" + cmd + "完成！");
		return cmd;
	}

	/**
	 * 添加Command到队列中
	 */
	public void enqueue(ZWICommand cmd) {
		ZWLogger.printLog(ZWCommandQueueManager.this, "添加" + cmd + "开始");
		queue.enqueue(cmd);
		ZWLogger.printLog(ZWCommandQueueManager.this, "添加" + cmd + "完成");
	}

	/**
	 * 清除队列
	 */
	public void clear() {
		queue.clear();
	}

	/**
	 * 关闭队列
	 */
	public void shutdown() {
		if (initialized) {
			queue.clear();
			pool.shutdown();
			initialized = false;
		}
	}
}
