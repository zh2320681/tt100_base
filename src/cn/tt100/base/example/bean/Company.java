package cn.tt100.base.example.bean;

import java.util.List;

import cn.tt100.base.ZWBo;
import cn.tt100.base.annotation.DatabaseField;
import cn.tt100.base.annotation.DatabaseTable;

@DatabaseTable(tableName="_Company")
public class Company extends ZWBo{

  @DatabaseField
  public String companyName;

  @DatabaseField(columnName="_ID", id=true)
  public int id;

  @DatabaseField
  public String info;
  
  @DatabaseField
  public boolean isITCompany;
  
  public String remark;
  
//  @DatabaseField(foreign = true, foreignColumnName=)
  public List<Employee> allWorks;
  
  
  public Company() {
	// TODO Auto-generated constructor stub
	  super();
  }
  
  
  /**
   * �������ݿ�� �ظ����ݹ���
   */
  @Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
	  if(o instanceof Company){
		  Company c = (Company)o;
		  return c.id == this.id;
	  }
		return super.equals(o);
	}


@Override
public String toString() {
	return "Company [companyName=" + companyName + ", id=" + id + ", info="
			+ info + ", isITCompany=" + isITCompany + ", remark=" + remark
			+ ", allWorks=" + allWorks + "]";
}
  
  
}
