package cn.shrek.base.example.appData;

import android.content.Context;
import cn.shrek.base.annotation.DataSave;
import cn.shrek.base.ormlite.foreign.MappingType;
import cn.shrek.base.util.data.ZWAppData;

public class MyAppData extends ZWAppData {
	
	@DataSave(defaultInteger=1987)
	public int parInt;
	
	@DataSave(isSecret = true,defaultString="中文测试")
	public String acount;
	
	@DataSave(isSecret = true)
	public String password;
	
	@DataSave(isSecret = true,defaultBoolean = true)
	public boolean sercetFlag;
	
	@DataSave(isSecret = true,defaultInteger = 1014)
	public int sercetIndex;
	
	@DataSave(defaultBoolean = false)
	public boolean isSave;
	
	@DataSave(isSecret = true,defaultString = "MANY_TO_MANY")
	public MappingType eumnType;

	public MyAppData(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "MyAppData [parInt=" + parInt + ", acount=" + acount
				+ ", password=" + password + ", isSave=" + isSave + ", eumnType=" + eumnType.name()+ "]";
	}

	
	
}
