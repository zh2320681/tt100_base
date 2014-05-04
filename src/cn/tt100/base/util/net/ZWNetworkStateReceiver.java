package cn.tt100.base.util.net;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cn.tt100.base.util.ZWLogger;
import cn.tt100.base.util.net.ZWNetWorkUtil.NetType;

/**
 * @Description 是一个检测网络状态改变的，需要配置 <receiver
 *              android:name="cn.tt100.base.util.net.ZWNetworkStateReceiver" >
 *              <intent-filter> <action
 *              android:name="android.net.conn.CONNECTIVITY_CHANGE" /> <action
 *              android:name="android.gzcpc.conn.CONNECTIVITY_CHANGE" />
 *              </intent-filter> </receiver>
 * 
 *              需要开启权限 <uses-permission
 *              android:name="android.permission.CHANGE_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.CHANGE_WIFI_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_WIFI_STATE" />
 */
public class ZWNetworkStateReceiver extends BroadcastReceiver {
	private static Boolean networkAvailable = false;
	private static NetType netType;
	private static ArrayList<ZWNetChangeObserver> netChangeObserverArrayList = new ArrayList<ZWNetChangeObserver>();
	private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public final static String TA_ANDROID_NET_CHANGE_ACTION = "ta.android.net.conn.CONNECTIVITY_CHANGE";
	
	private static BroadcastReceiver receiver;

	private static BroadcastReceiver getReceiver() {
		if (receiver == null) {
			receiver = new ZWNetworkStateReceiver();
		}
		return receiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		receiver = ZWNetworkStateReceiver.this;
		if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)
				|| intent.getAction().equalsIgnoreCase(
						TA_ANDROID_NET_CHANGE_ACTION)) {
			ZWLogger.printLog(ZWNetworkStateReceiver.this, "网络状态改变.");
			if (!ZWNetWorkUtil.isNetworkAvailable(context)) {
				ZWLogger.printLog(ZWNetworkStateReceiver.this, "没有网络连接.");
				networkAvailable = false;
			} else {
				ZWLogger.printLog(ZWNetworkStateReceiver.this, "网络连接成功.");
				netType = ZWNetWorkUtil.getAPNType(context);
				networkAvailable = true;
			}
			notifyObserver();
		}
	}

	/**
	 * 注册网络状态广播
	 * 
	 * @param mContext
	 */
	public static void registerNetworkStateReceiver(Context mContext) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(TA_ANDROID_NET_CHANGE_ACTION);
		filter.addAction(ANDROID_NET_CHANGE_ACTION);
		mContext.getApplicationContext()
				.registerReceiver(getReceiver(), filter);
	}

	/**
	 * 检查网络状态
	 * 
	 * @param mContext
	 */
	public static void checkNetworkState(Context mContext) {
		Intent intent = new Intent();
		intent.setAction(TA_ANDROID_NET_CHANGE_ACTION);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 注销网络状态广播
	 * 
	 * @param mContext
	 */
	public static void unRegisterNetworkStateReceiver(Context mContext) {
		if (receiver != null) {
			try {
				mContext.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e) {
				// TODO: handle exception
				ZWLogger.printLog("TANetworkStateReceiver", e.getMessage());
			}
		}

	}

	/**
	 * 获取当前网络状态，true为网络连接成功，否则网络连接失败
	 * 
	 * @return
	 */
	public static Boolean isNetworkAvailable() {
		return networkAvailable;
	}

	public static NetType getAPNType() {
		return netType;
	}

	private void notifyObserver() {
		for (int i = 0; i < netChangeObserverArrayList.size(); i++) {
			ZWNetChangeObserver observer = netChangeObserverArrayList.get(i);
			if (observer != null) {
				if (isNetworkAvailable()) {
					observer.onConnect(netType);
				} else {
					observer.onDisConnect();
				}
			}
		}
	}

	/**
	 * 注册网络连接观察者
	 * 
	 * @param observerKey
	 *            observerKey
	 */
	public static void registerObserver(ZWNetChangeObserver observer) {
		if (netChangeObserverArrayList == null) {
			netChangeObserverArrayList = new ArrayList<ZWNetChangeObserver>();
		}
		netChangeObserverArrayList.add(observer);
	}

	/**
	 * 注销网络连接观察者
	 * 
	 * @param resID
	 *            observerKey
	 */
	public static void removeRegisterObserver(ZWNetChangeObserver observer) {
		if (netChangeObserverArrayList != null) {
			netChangeObserverArrayList.remove(observer);
		}
	}

}