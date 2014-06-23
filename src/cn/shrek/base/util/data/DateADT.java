package cn.shrek.base.util.data;

import java.sql.Date;

public class DateADT implements AppDataTransfor<Date> {

	@Override
	public String toString(Date t) {
		// TODO Auto-generated method stub
		return t.getTime()+"";
	}

	@Override
	public Date parse2Obj(String str) {
		// TODO Auto-generated method stub
		long timeStmp = Long.parseLong(str);
		return new Date(timeStmp);
	}

}
