package cn.shrek.base.example.bean;

import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;
import cn.shrek.base.annotation.Foreign;
import cn.shrek.base.event.ZWEventPara;
import cn.shrek.base.ormlite.foreign.CascadeType;
import cn.shrek.base.ormlite.foreign.MappingType;
import cn.shrek.base.ui.inject.Identity;

@DatabaseTable
public class CEO extends ZWDatabaseBo implements Identity, ZWEventPara {

	/**
	 * 
	 */
	private static final long serialVersionUID = -608798915010079053L;

	@Foreign(foreignColumnName = "comId", originalColumnName = "ceoId", mappingType = MappingType.ONE_TO_MANY,cascade={CascadeType.ALL})
	public Company company;

	@DatabaseField(id = true)
	public int ceoId;

	@DatabaseField //(unique = true)
	public String name;

	public CEO() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public int getIdentityID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub

	}

}
