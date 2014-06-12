package cn.shrek.base.example;

import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.net.ZWNetworkStateReceiver;
import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;

/**
 * �������
 * @author shrek
 *
 */
public class NetTestActivity extends ZWActivity {
	@AutoInject(idFormat = "nt_?")
	private TextView infoView;
	
	
	private StringBuffer sb;
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		sb = new StringBuffer();
		ZWNetworkStateReceiver.registerNetworkStateReceiver(this);
	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ZWNetworkStateReceiver.unRegisterNetworkStateReceiver(this);
	}
	
	@Override
	public void onConnect(NetType type) {
		// TODO Auto-generated method stub
		super.onConnect(type);
		sb.append("�����Ѿ�����,��������Ϊ:"+ type.toString()+ "\n");
		infoView.setText(sb.toString());
	}

	@Override
	public void onDisConnect() {
		// TODO Auto-generated method stub
		super.onDisConnect();
		sb.append("����Ͽ�����!\n");
		infoView.setText(sb.toString());
	}
	
	

}
