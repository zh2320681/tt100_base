package cn.shrek.base.example;

import java.util.List;
import java.util.logging.Logger;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.ZWLogger;
import cn.shrek.base.util.logger.LoggerBo;
import cn.shrek.base.util.net.ZWNetWorkUtil.NetType;
import cn.shrek.base.util.net.ZWNetworkStateReceiver;
import cn.tt100.base.R;

/**
 * log测试
 * @author shrek
 *
 */

@Controller(layoutId = R.layout.logtest)
public class LogTestActivity extends ZWActivity {
	@AutoInject(idFormat = "lt_?",clickSelector="myClick")
	private Button closeLogBtn,printLogBtn,readLogBtn;
	
	OnClickListener myClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == closeLogBtn){
				((ZWApplication)getApplication()).closeLoggerPrint();
				Toast.makeText(getApplicationContext(), "日志打印已关闭!", Toast.LENGTH_LONG).show();
			}else if(v == printLogBtn){
				ZWLogger.p("测试", "本地打印1");
				ZWLogger.p("测试", "本地打印2");
				ZWLogger.p("测试", "本地打印3");
				ZWLogger.p("测试", "本地打印4");
				ZWLogger.p("测试", "本地打印5");
				ZWLogger.p("测试", "本地打印6");
				ZWLogger.p("测试", "本地打印7");
				
				Toast.makeText(getApplicationContext(), "日志打印成功!", Toast.LENGTH_LONG).show();
			}else if(v == readLogBtn){
				List<LoggerBo> bos = ((ZWApplication)getApplication()).getHistoryLogs();
				for(LoggerBo bo : bos){
					System.out.println(bo);
				}
			}
		}
	};
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
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
	}
	
	@Override
	public void onConnect(NetType type) {
		// TODO Auto-generated method stub
		super.onConnect(type);
		
	}

	@Override
	public void onDisConnect() {
		// TODO Auto-generated method stub
		super.onDisConnect();
	}
	
	

}
