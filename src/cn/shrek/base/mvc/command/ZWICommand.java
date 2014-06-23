package cn.shrek.base.mvc.command;

import cn.shrek.base.mvc.common.ZWIResponseListener;
import cn.shrek.base.mvc.common.ZWRequest;
import cn.shrek.base.mvc.common.ZWResponse;

/**
 * @Description ZWICommand一个命令接口所有命令需要从此实现
 * @author Shrek
 */
public interface ZWICommand{
	ZWRequest getRequest();

	void setRequest(ZWRequest request);

	ZWResponse getResponse();

	void setResponse(ZWResponse response);

	void execute();

	ZWIResponseListener getResponseListener();

	void setResponseListener(ZWIResponseListener listener);

	void setTerminated(boolean terminated);

	boolean isTerminated();

}
