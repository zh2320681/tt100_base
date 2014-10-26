package cn.shrek.base.ormlite.dao;

import java.util.Calendar;
import java.util.Date;

import cn.shrek.base.ormlite.DBUtil;

public class CalendarTransfor implements DBTransforDao<Calendar, String> {

	@Override
	public String parseFieldToColumn(Calendar fieldObj) {
		// TODO Auto-generated method stub
		Date date = fieldObj.getTime();
		return String.format("%tF %tT",date,date);
	}

	@Override
	public Calendar parseColumnToField(String columnObj) {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		cal.setTime(DBUtil.getFormatDate(columnObj));
		return cal;
	}

	
	@Override
	public void specialDoing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getFeildValueNull() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFeildNullFeild(Calendar f) {
		// TODO Auto-generated method stub
		return f==null;
	}
	
}
