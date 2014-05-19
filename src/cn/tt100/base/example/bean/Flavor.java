package cn.tt100.base.example.bean;

/**
 * 菜品口味实体类
 * 
 * @author david
 * 
 */
public class Flavor {
	public String id;
	public String name;
	public String updated;

	@Override
	public String toString() {
		return "Flavor [id=" + id + ", name=" + name + ", updated=" + updated
				+ "]";
	}

}
