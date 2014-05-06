package cn.tt100.base.ormlite.stmt;

import android.content.ContentValues;
import cn.tt100.base.ZWBo;

public class UpdateBuilder<T extends ZWBo> extends StmtBuilder {
	public ContentValues cvs;
	
	public UpdateBuilder(Class<T> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSql() {
		// TODO Auto-generated method stub
		return null;
	}

}
