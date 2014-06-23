package cn.shrek.base.mvc.command;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import cn.shrek.base.exception.TANoSuchCommandException;
import cn.shrek.base.mvc.common.ZWIResponseListener;
import cn.shrek.base.mvc.common.ZWRequest;
import cn.shrek.base.util.ZWLogger;

public class ZWCommandExecutor {
	private final HashMap<String, Class<? extends ZWICommand>> commands = new HashMap<String, Class<? extends ZWICommand>>();

	private static final ZWCommandExecutor instance = new ZWCommandExecutor();
	private boolean initialized = false;

	
	public ZWCommandExecutor() {
		if (!initialized) {
			initialized = true;
			ZWLogger.printLog(ZWCommandExecutor.this, "CommandExecutor初始化");
			ZWCommandQueueManager.getInstance().initialize();
			ZWLogger.printLog(ZWCommandExecutor.this, "CommandExecutor初始化");
		}
	}

	public static ZWCommandExecutor getInstance() {
		return instance;
	}

	/**
	 * 所有命令终止或标记为结束
	 */
	public void terminateAll() {

	}

	/**
	 * 命令入列
	 * 
	 * @param commandKey
	 *            命令ID
	 * @param request
	 *            提交的参数
	 * @param listener
	 *            响应监听器
	 * @throws TANoSuchCommandException
	 */
	public void enqueueCommand(String commandKey, ZWRequest request,
			ZWIResponseListener listener) throws TANoSuchCommandException {
		final ZWICommand cmd = getCommand(commandKey);
		enqueueCommand(cmd, request, listener);
	}

	public void enqueueCommand(ZWICommand command, ZWRequest request,
			ZWIResponseListener listener) throws TANoSuchCommandException {
		if (command != null) {
			command.setRequest(request);
			command.setResponseListener(listener);
			ZWCommandQueueManager.getInstance().enqueue(command);
		}
	}

	public void enqueueCommand(ZWICommand command, ZWRequest request)
			throws TANoSuchCommandException {
		enqueueCommand(command, null, null);
	}

	public void enqueueCommand(ZWICommand command)
			throws TANoSuchCommandException {
		enqueueCommand(command, null);
	}

	
	/**
	 * 得到命令
	 * @param commandKey
	 * @return
	 * @throws TANoSuchCommandException
	 */
	private ZWICommand getCommand(String commandKey)
			throws TANoSuchCommandException {
		ZWICommand rv = null;

		if (commands.containsKey(commandKey)) {
			Class<? extends ZWICommand> cmd = commands.get(commandKey);
			if (cmd != null) {
				int modifiers = cmd.getModifiers();
				if ((modifiers & Modifier.ABSTRACT) == 0
						&& (modifiers & Modifier.INTERFACE) == 0) {
					try {
						rv = cmd.newInstance();
					} catch (Exception e) {
						throw new TANoSuchCommandException("没发现" + commandKey
								+ "命令");
					}
				} else {
					throw new TANoSuchCommandException("没发现" + commandKey
							+ "命令");
				}
			}
		}

		return rv;
	}

	public void registerCommand(String commandKey,
			Class<? extends ZWICommand> command) {
		if (command != null) {
			commands.put(commandKey, command);
		}
	}

	public void unregisterCommand(String commandKey) {
		commands.remove(commandKey);
	}
}
