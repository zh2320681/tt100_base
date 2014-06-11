package cn.shrek.base;

import java.util.Observable;
import java.util.Observer;

public class ModelObservable extends Observable {
	private Object data;
	
	public ModelObservable(Object data){
		this.data = data;
	}
	
	@Override
	public void addObserver(Observer observer) {
		// TODO Auto-generated method stub
		super.addObserver(observer);
	}

	public void changeData(Object newData){
		setChanged();
		notifyObservers(newData);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	
}
