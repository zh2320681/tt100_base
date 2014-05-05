package cn.tt100.base.example.bean;

import java.util.Date;

import cn.tt100.base.ZWBo;
import cn.tt100.base.annotation.DatabaseField;
import cn.tt100.base.annotation.DatabaseTable;

@DatabaseTable
public class Employee extends ZWBo {

	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignColumnName = "id")
	public Company company;

	@DatabaseField(canBeNull = false)
	public Date createTime;

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
}