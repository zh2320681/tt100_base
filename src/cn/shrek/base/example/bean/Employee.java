package cn.shrek.base.example.bean;

import cn.shrek.base.ZWBo;
import cn.shrek.base.ZWDatabaseBo;
import cn.shrek.base.annotation.DatabaseField;
import cn.shrek.base.annotation.DatabaseTable;
import cn.shrek.base.event.ZWEventPara;
import cn.shrek.base.ui.inject.Identity;

@DatabaseTable
public class Employee extends ZWDatabaseBo implements Identity,ZWEventPara{

	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignColumnName = "id")
	public Company company;

//	@DatabaseField(canBeNull = false)
//	public Date createTime;

	@DatabaseField(id = true)
	public int id;

	@DatabaseField(columnName = "__new")
	public boolean isNew;

	@DatabaseField(unique = true)
	public String name;

	@DatabaseField(columnName = "__index1", index = true)
	public String index1;
	@DatabaseField(index = true)
	public String index2;

	// @DatabaseField(foreign=true, foreignAutoRefresh=true,
	// foreignColumnName="id")
	// public List<CompanyWork> works;
	
	
	public Employee() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public String toString() {
		return "Employee [company=" + company + ", id=" + id + ", isNew="
				+ isNew + ", name=" + name + ", index1=" + index1 + ", index2="
				+ index2 + "]";
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
