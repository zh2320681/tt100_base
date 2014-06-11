package cn.shrek.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * 异常类
 * @author shrek
 *
 */
public class ErrorActivity extends ZWActivity {
	public static Throwable ex;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		super.onBaseCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.BLACK);
		setContentView(layout);
		if(ex != null){
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this).setTitle("提示")
					.setCancelable(false).setMessage("程序崩溃了...")
					.setNeutralButton("关闭APP", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							android.os.Process
									.killProcess(android.os.Process.myPid());
							System.exit(10);
						}
					}).setNeutralButton("重新启动", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());  
					        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
					        startActivity(intent);
						}
						
					});
					if(ZWApplication.isDebugMode){
						mBuilder.setNeutralButton("debug", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
							}
							
						});
					}
					mBuilder.create().show();
		}
	}
	
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

}
