package cn.shrek.base.ormlite.foreign;

/**
 * 级联操作类型
 * @author shrek
 *
 */
public enum CascadeType {
	/**
	 * 级联新建
	 */
	PERSIST
	/**
	 * 级联删除
	 */
	, REMOVE
	/**
	 * 级联刷新
	 */
	, REFRESH
	/**
	 * （级联更新）中选择一个或多个。
	 */
	, MERGE

	/**
	 * 表示选择全部四项
	 */
	, ALL;
}
