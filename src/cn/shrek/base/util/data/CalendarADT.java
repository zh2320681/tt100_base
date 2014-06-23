package cn.shrek.base.util.data;

import java.sql.Date;
import java.util.Calendar;

public class CalendarADT implements AppDataTransfor<Calendar> {

	@Override
	public String toString(Calendar t) {
		// TODO Auto-generated method stub
		return t.getTime().getTime()+"";
	}

	@Override
	public Calendar parse2Obj(String str) {
		// TODO Auto-generated method stub
		long timeStmp = Long.parseLong(str);
		Date date = new Date(timeStmp);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

}
