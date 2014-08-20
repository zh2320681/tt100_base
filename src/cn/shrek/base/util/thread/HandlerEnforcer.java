package cn.shrek.base.util.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import cn.shrek.base.ZWConstants;
import cn.shrek.base.event.ThreadMode;
import cn.shrek.base.ui.inject.Identity;

public class HandlerEnforcer implements ZWThreadEnforcer,Identity {
	private static HandlerEnforcer enforcer;
	
	private Handler mHandler,backgroudHandler;
	private HandlerThread mHandlerThread;
	
	public static HandlerEnforcer newInstance(){
		if(enforcer == null){
			enforcer = new HandlerEnforcer();
		}
		return enforcer;
	}
	
	private HandlerEnforcer(){
		super();
		
	}

	public Handler getmHandler() {
		if(mHandler == null){
			mHandler = new Handler(Looper.getMainLooper());
		}
		return mHandler;
	}

	public Handler getBackgroudHandler() {
		if(mHandlerThread == null){
			mHandlerThread = new HandlerThread(ZWConstants.THREAD_ENFORCER);
			mHandlerThread.start();
			
			backgroudHandler = new Handler(mHandlerThread.getLooper());
		}
		return backgroudHandler;
	}


	@Override
	public void enforceMainThread(Runnable run) {
		// TODO Auto-generated method stub
		enforceMainThreadDelay(run, 0);
	}

	@Override
	public void enforceMainThreadDelay(Runnable run, long millisecond) {
		// TODO Auto-generated method stub
		enforceDelay(ThreadMode.MainThread, run, millisecond);
	}

	@Override
	public void enforceBackgroud(Runnable run) {
		// TODO Auto-generated method stub
		enforceBackgroudDelay(run, 0);
	}

	@Override
	public void enforceBackgroudDelay(Runnable run, long millisecond) {
		// TODO Auto-generated method stub
		enforceDelay(ThreadMode.BackgroundThread, run, millisecond);
	}

	@Override
	public void enforce(ThreadMode tMode, Runnable run) {
		// TODO Auto-generated method stub
		enforceDelay(tMode, run, 0);
	}

	@Override
	public void enforceDelay(ThreadMode tMode, Runnable run, long millisecond) {
		// TODO Auto-generated method stub
		if(run == null){
			throw new IllegalArgumentException("Thread enforcer Runnable must not null");
		}
		
		if(millisecond < 0){
			throw new IllegalArgumentException("Thread enforcer millisecond > 0");
		}
		switch (tMode) {
		case MainThread:
			getmHandler().postDelayed(run, millisecond);
			break;
		case BackgroundThread:
			getBackgroudHandler().postDelayed(run, millisecond);
			break;
		case PostThread:
		default:
			run.run();
			break;
		}
	}

	@Override
	public int getIdentityID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		mHandler = null;
		
		if(mHandlerThread != null){
			mHandlerThread.quitSafely();
			mHandlerThread.interrupt();
		}
		
		mHandlerThread = null;
		backgroudHandler = null;
	}

}
