package cn.shrek.base.example.bean;

import java.util.List;

import android.os.Message;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;
import cn.shrek.base.annotation.Foreign;
import cn.shrek.base.ormlite.foreign.CascadeType;
import cn.shrek.base.ormlite.foreign.MappingType;
import cn.shrek.base.ui.inject.Identity;

@DatabaseTable(tableName = "_Company")
public class Company extends ZWDatabaseBo implements Identity {

	@DatabaseField
	public String companyName;

	@DatabaseField(columnName = "_ID", id = true)
	public int comId;

	@DatabaseField
	public String info;

	@DatabaseField
	public boolean isITCompany;

	public String remark;

	@Foreign(foreignColumnName = "id", originalColumnName = "comId", mappingType = MappingType.MANY_TO_ONE, cascade = { CascadeType.ALL })
	public List<Employee> allWorks;

	@Foreign(foreignColumnName = "ceoId", originalColumnName = "comId", mappingType = MappingType.MANY_TO_ONE, cascade = {})
	public List<CEO> otherWorks;
	
	public Company() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof Company) {
			Company c = (Company) o;
			return c.comId == this.comId;
		}
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return comId;
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

	@Override
	public String toString() {
		return "Company [companyName=" + companyName + ", comId=" + comId
				+ ", info=" + info + ", isITCompany=" + isITCompany
				+ ", remark=" + remark + ", allWorks=" + allWorks + "]";
	}

}
