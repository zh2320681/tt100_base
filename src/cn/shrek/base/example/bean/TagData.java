package cn.shrek.base.example.bean;

import java.util.Random;

public class TagData {
	
	static final String[] s = new String[] { "张三", "李四", "王五", "赵六", "田七", "五八",
			"马九","张三1", "李四1", "王五1", "赵六1", "田七1", "五八1",
			"马九1" };
	static final Random ran = new Random();
	
	public String name;
	public boolean isCheck;
	public int age;
	
	public TagData(){
		int i = ran.nextInt(s.length);
		name = s[i];
		age = ran.nextInt(100);
		
		isCheck = false;
	}
	
	
}
