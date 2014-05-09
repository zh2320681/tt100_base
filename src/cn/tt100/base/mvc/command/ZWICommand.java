package cn.tt100.base.mvc.command;

import cn.tt100.base.mvc.common.ZWIResponseListener;
import cn.tt100.base.mvc.common.ZWRequest;
import cn.tt100.base.mvc.common.ZWResponse;

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
