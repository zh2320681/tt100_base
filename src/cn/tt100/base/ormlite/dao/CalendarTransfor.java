package cn.tt100.base.ormlite.dao;

import java.util.Calendar;

public class CalendarTransfor implements DBTransforDao<Calendar, Long> {

	@Override
	public Long parseFieldToColumn(Calendar fieldObj) {
		// TODO Auto-generated method stub
		return fieldObj.getTimeInMillis();
	}

	@Override
	public Calendar parseColumnToField(Long columnObj) {
		// TODO Auto-generated method stub
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(columnObj);
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
