package cn.tt100.base.example.bean;


public class City {
	private static final String DEFAULT_ID = "1";
	private static final String DEFAULT_NAME = "张家港";
	
	public String id;
	public String name;
	public Province province;
	
	public City(){
		
	}
	
	/**
	 * 得到默认的城市
	 * @return
	 */
	public static City getDefaultCity(){
		City defaultCity = new City();
		defaultCity.id = DEFAULT_ID;
		defaultCity.name = DEFAULT_NAME;
		
		defaultCity.province = new Province();
		defaultCity.province.id = "1";
		defaultCity.province.name = "江苏";
		return defaultCity;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof City){
			City city = (City)o;
			return city.id == this.id;
		}
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", province=" + province
				+ "]";
	}
	
	
	 
}
