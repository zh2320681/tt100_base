package cn.shrek.base.util.data;

public class BooelanADT implements AppDataTransfor<Boolean> {
	static final String TRUE_STR = "TRUE";
	static final String FALSE_STR = "FALSE";
	@Override
	public String toString(Boolean t) {
		// TODO Auto-generated method stub
		return t?TRUE_STR:FALSE_STR;
	}

	@Override
	public Boolean parse2Obj(String str) {
		// TODO Auto-generated method stub
		if(TRUE_STR.equalsIgnoreCase(str)){
			return true;
		}
		return false;
	}

}
