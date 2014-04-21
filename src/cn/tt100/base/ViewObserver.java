package cn.tt100.base;

import java.util.Observable;
import java.util.Observer;

public abstract class ViewObserver<T> implements Observer {
	private T obServer;
	
	public ViewObserver(T obServer){
		this.obServer = obServer;
	}
	
	@Override
	public abstract void update(Observable mObservable, Object arg1);

	public T getObServer() {
		return obServer;
	}


}
