package cn.shrek.base.util;

import android.os.Build;

/**
 * @Title AndroidVersionCheckUtils
 * @Description AndroidVersionCheckUtils ���ڶ�汾���ݼ��
 */
public class AndroidVersionCheckUtils {
	
	private AndroidVersionCheckUtils() {
		
	};

	/**
	 * ��ǰAndroidϵͳ�汾�Ƿ��ڣ� Donut�� Android 1.6������
	 * 
	 * @return
	 */
	public static boolean hasDonut() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT;
	}

	/**
	 * ��ǰAndroidϵͳ�汾�Ƿ��ڣ� Eclair�� Android 2.0�� ����
	 * 
	 * @return
	 */
	public static boolean hasEclair() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
	}

	/**
	 * ��ǰAndroidϵͳ�汾�Ƿ��ڣ� Froyo�� Android 2.2�� Android 2.2����
	 * 
	 * @return
	 */
	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * ��ǰAndroidϵͳ�汾�Ƿ��ڣ� Gingerbread�� Android 2.3x�� Android 2.3x ����
	 * 
	 * @return
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * ��ǰAndroidϵͳ�汾�Ƿ��ڣ� Honeycomb�� Android3.1�� Android3.1����
	 * 
	 * @return
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * ��ǰAndroidϵͳ�汾�Ƿ��ڣ� HoneycombMR1�� Android3.1.1�� Android3.1.1����
	 * 
	 * @return
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * ��ǰAndroidϵͳ�汾�Ƿ��ڣ� IceCreamSandwich�� Android4.0�� Android4.0����
	 * 
	 * @return
	 */
	public static boolean hasIcecreamsandwich() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	/*
	 * public static boolean hasJellyBean() { return Build.VERSION.SDK_INT >=
	 * Build.VERSION_CODES.JELLY_BEAN; }
	 */
}
