package cn.shrek.base.util.data;

public interface AppDataTransfor<T> {
	
	String toString(T t);
	
	T parse2Obj(String str);
	
}
