package cn.shrek.base.event;

import cn.shrek.base.ui.inject.Identity;
import cn.shrek.base.util.ZWLogger;

public class ZWEvent implements Identity {
	private static final int MAX_POOL_SIZE = 10;

	private static final int FLAG_IN_USE = 1 << 0;

	// TAG标识
	String eventTag;

	// 支持int 类型
	int eventFlag;
	
	// 作为参数和匹配类型
	Class<? extends ZWEventPara> objClazz;

	ZWEventPara obj;

	ZWEvent next;

	// 支持延迟发送
	long when;

	int flags;

	private static final Object sPoolSync = new Object();
	private static ZWEvent sPool;
	private static int sPoolSize = 0;

	private ZWEvent() {

	}

	public static ZWEvent obtain() {
		synchronized (sPoolSync) {
			if (sPool != null) {
				ZWEvent event = sPool;
				sPool = event.next;
				event.next = null;
				sPoolSize--;
				return event;
			}
		}
		return new ZWEvent();
	}

	public static ZWEvent obtain(ZWEvent orig) {
		ZWEvent event = obtain();
		event.eventTag = orig.eventTag;
		event.eventFlag = orig.eventFlag;
		event.objClazz = orig.objClazz;

		return event;
	}

	public static ZWEvent obtain(String eventTag, int eventFlag,
			Class<? extends ZWEventPara> clazz) {
		ZWEvent event = obtain();
		event.eventTag = eventTag;
		event.eventFlag = eventFlag;
		event.objClazz = clazz;
		return event;
	}

	public static ZWEvent obtainObj(String eventTag, int eventFlag,
			ZWEventPara obj) {
		ZWEvent event = obtain();
		event.eventTag = eventTag;
		event.eventFlag = eventFlag;
		if (obj != null) {
			event.objClazz = obj.getClass();
		}

		event.obj = obj;

		return event;
	}
	

	public long getWhen() {
		return when;
	}

	boolean isInUse() {
		return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
	}

	void markInUse() {
		flags |= FLAG_IN_USE;
	}

	@Override
	public int getIdentityID() {
		// TODO Auto-generated method stub
		return 0;
	}

	void clearForRecycle() {
		eventTag = null;
		eventFlag = 0;
		objClazz = null;

		when = 0;
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		ZWLogger.printLog(ZWEvent.class, toString() + "  释放到池中!");
		clearForRecycle();

		synchronized (sPoolSync) {
			if (sPoolSize < MAX_POOL_SIZE) {
				next = sPool;
				sPool = this;
				sPoolSize++;
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof ZWEvent) {
			ZWEvent event = (ZWEvent) o;
			if ((event.objClazz == null && objClazz != null)
					|| (objClazz == null && event.objClazz != null)) {
				return false;
			}
			return event.eventFlag == eventFlag
					&& event.eventTag.equals(eventTag)
					&& (event.objClazz == null ? objClazz == null
							: event.objClazz.isAssignableFrom(objClazz));
		}
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "ZWEvent [eventTag=" + eventTag + ", eventFlag=" + eventFlag
				+ ", objClazz=" + objClazz + "]";
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return toString().hashCode();
	}

}
