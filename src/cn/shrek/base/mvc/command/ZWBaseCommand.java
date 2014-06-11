package cn.shrek.base.mvc.command;

import cn.shrek.base.mvc.common.ZWIResponseListener;
import cn.shrek.base.mvc.common.ZWRequest;
import cn.shrek.base.mvc.common.ZWResponse;

public abstract class ZWBaseCommand implements ZWICommand {
	private ZWRequest request;
	private ZWResponse response;
	private ZWIResponseListener responseListener;
	private boolean terminated;

	@Override
	public ZWRequest getRequest() {
		// TODO Auto-generated method stub
		return request;
	}

	@Override
	public void setRequest(ZWRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	@Override
	public ZWResponse getResponse() {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public void setResponse(ZWResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}

	@Override
	public ZWIResponseListener getResponseListener() {
		// TODO Auto-generated method stub
		return responseListener;
	}

	@Override
	public void setResponseListener(ZWIResponseListener responseListener) {
		// TODO Auto-generated method stub
		this.responseListener = responseListener;
	}

	@Override
	public void setTerminated(boolean terminated) {
		// TODO Auto-generated method stub
		this.terminated = terminated;
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return terminated;
	}

}
