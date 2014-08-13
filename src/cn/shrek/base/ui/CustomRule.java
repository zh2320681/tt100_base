package cn.shrek.base.ui;

public interface CustomRule<SOURCE> {
	/**
	 * 规则判断
	 * @param orgin
	 * @return  是否符合规则
	 */
	boolean ruleJudge(SOURCE orgin);
}
