package cn.shrek.base.util.net;

import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;

public interface ZWNetChangeObserver {
	/**
	 * 网络连接连接时调用
	 */
	public void onConnect(NetType type);

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect();
}
