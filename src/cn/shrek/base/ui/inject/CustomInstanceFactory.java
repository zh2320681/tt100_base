package cn.shrek.base.ui.inject;

import java.util.Map;


/**
 * 自定义的 实例工厂
 * @author shrek
 *
 */
public interface CustomInstanceFactory {
	
	/**
	 * 通过tag 查找实力
	 * @param tag
	 * @return
	 */
	public Identity getInstanceByTag(String tag);
	
	/**
	 * 初始化 做什么
	 * @param injector
	 */
	public Map<Class<?>,Identity> getDefaultInstance();
}
