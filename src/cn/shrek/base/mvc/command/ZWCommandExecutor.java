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
			ZWLogger.printLog(ZWCommandExecutor.this, "CommandExecutor��ʼ��");
			ZWCommandQueueManager.getInstance().initialize();
			ZWLogger.printLog(ZWCommandExecutor.this, "CommandExecutor��ʼ��");
		}
	}

	public static ZWCommandExecutor getInstance() {
		return instance;
	}

	/**
	 * ����������ֹ����Ϊ����
	 */
	public void terminateAll() {

	}

	/**
	 * ��������
	 * 
	 * @param commandKey
	 *            ����ID
	 * @param request
	 *            �ύ�Ĳ���
	 * @param listener
	 *            ��Ӧ������
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
	 * �õ�����
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
						throw new TANoSuchCommandException("û����" + commandKey
								+ "����");
					}
				} else {
					throw new TANoSuchCommandException("û����" + commandKey
							+ "����");
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
