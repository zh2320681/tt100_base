package cn.shrek.base.util.net;

import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;

public interface ZWNetChangeObserver {
	/**
	 * ������������ʱ����
	 */
	public void onConnect(NetType type);

	/**
	 * ��ǰû����������
	 */
	public void onDisConnect();
}
