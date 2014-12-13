package cn.shrek.base.util.data;

public class EnumADT <T extends Enum> implements AppDataTransfor<T> {
	Class<T> enumInstance;
	
	public EnumADT(Class<T> clazz){
		super();
		this.enumInstance = clazz;
	}
	
	@Override
	public String toString(T t) {
		// TODO Auto-generated method stub
		return t.name();
	}

	@Override
	public T parse2Obj(String str) {
		// TODO Auto-generated method stub
		return (T) Enum.valueOf(enumInstance, str);
	}

}
