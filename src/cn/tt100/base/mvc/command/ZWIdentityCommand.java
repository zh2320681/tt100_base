package cn.tt100.base.mvc.command;

import cn.tt100.base.mvc.common.ZWIResponseListener;
import cn.tt100.base.mvc.common.ZWRequest;
import cn.tt100.base.mvc.common.ZWResponse;

public class ZWIdentityCommand extends ZWCommand {
	@Override
	protected void executeCommand() {
		// TODO Auto-generated method stub
		ZWRequest request = getRequest();
		ZWResponse response = new ZWResponse();
		response.setTag(request.getTag());
		response.setData(request.getData());
		response.setActivityKey((String) request.getActivityKey());
		response.setActivityKeyResID(request.getActivityKeyResID());
		setResponse(response);
		notifyListener(true);
	}

	protected void notifyListener(boolean success) {
		ZWIResponseListener responseListener = getResponseListener();
		if (responseListener != null) {
			sendMessage(command_success);
		}
	}
}
