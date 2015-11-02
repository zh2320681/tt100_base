package cn.shrek.base.example;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;
import cn.shrek.base.util.net.ZWNetworkStateReceiver;

/**
 * 网络测试
 * @author shrek
 *
 */

@Controller(isMonitorNetwork = true,idFormat = "nt_?")
public class NetTestActivity extends ZWActivity {
	@AutoInject
	private TextView infoView;
	
	@AutoInject(clickSelector="myClick")
	Button futureBtn;
	
	private StringBuffer sb;
	
	private OnClickListener myClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			sb.append("Future任务开始!\n");
			infoView.setText(sb.toString());
			
			FutureTask<String> task = new FutureTask<String>(new Callable<String>() {

				@Override
				public String call() throws Exception {
					// TODO Auto-generated method stub
					Thread.sleep(20000);
					return null;
				}
			});
			
			new Thread(task).start();
			
			sb.append("Future任务开始执行,20秒后取结果!\n");
			infoView.setText(sb.toString());
			
			try {
				task.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sb.append("Future任务结束!\n");
			infoView.setText(sb.toString());
		}
	};
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		sb = new StringBuffer();
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
		sb.append("网络已经连接,连接类型为:"+ type.toString()+ "\n");
		infoView.setText(sb.toString());
	}

	@Override
	public void onDisConnect() {
		// TODO Auto-generated method stub
		super.onDisConnect();
		sb.append("网络断开连接!\n");
		infoView.setText(sb.toString());
	}
	
	

}
