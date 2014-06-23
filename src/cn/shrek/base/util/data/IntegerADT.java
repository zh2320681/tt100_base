package cn.shrek.base.util.data;

public class IntegerADT implements AppDataTransfor<Integer> {

	@Override
	public String toString(Integer t) {
		// TODO Auto-generated method stub
		return t+"";
	}

	@Override
	public Integer parse2Obj(String str) {
		// TODO Auto-generated method stub
		return Integer.parseInt(str);
	}

}
