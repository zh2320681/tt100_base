package cn.shrek.base.example.appData;

import android.content.Context;
import cn.shrek.base.annotation.DataSave;
import cn.shrek.base.util.data.ZWAppData;

public class MyAppData extends ZWAppData {
	
	@DataSave(defaultInteger=1987)
	public int parInt;
	
	@DataSave(isSecret = true,defaultString="ADMIN")
	public String acount;
	
	@DataSave(isSecret = true)
	public String password;
	
	@DataSave(defaultBoolean = false)
	public boolean isSave;

	public MyAppData(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

}
