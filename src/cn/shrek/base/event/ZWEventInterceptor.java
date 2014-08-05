package cn.shrek.base.event;

/**
 * 事件拦截器
 * @author shrek
 *
 */
public abstract class ZWEventInterceptor {

	ZWEventBus bus;
	
	public ZWEventInterceptor(){
		super();
	}

	public void setBus(ZWEventBus bus) {
		if(bus == null){
			throw new IllegalArgumentException("事件拦截器的 event bus不能为NULL!");
		}
		this.bus = bus;
	}


	public void destroy(){
		bus.unregisterInterceptor();
		bus = null;
	}
	
	protected final void resendEvent(ZWEvent event){
		bus.post(event, false);
	}
}
