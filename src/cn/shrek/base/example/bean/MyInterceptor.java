package cn.shrek.base.example.bean;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.annotation.Subscribe;
import cn.shrek.base.event.ZWEvent;
import cn.shrek.base.event.ZWEventInterceptor;
import cn.shrek.base.example.fragment.EventFragment1;

/**
 * 自定义拦截器
 * @author shrek
 *
 */
public class MyInterceptor extends ZWEventInterceptor {
	ZWApplication app;
	Activity ctx;
	public MyInterceptor(Activity ctx) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
		app = (ZWApplication)ctx.getApplication();
	}

	@Subscribe(tag=EventFragment1.EVENT1)
	public boolean invokeTest(ZWEvent event){
		Toast.makeText(ctx, "妈的,什么叫拦截器", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	@Subscribe(tag=EventFragment1.EVENT2)
	public boolean invokeTest1(final ZWEvent event){
		new AlertDialog.Builder(app.getCurrActivity()).setMessage("点我后才能发消息").setTitle("拦截器")
		.setPositiveButton("点我发送", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				resendEvent(event);
			}
		}).create().show();
		return false;
	}
}
