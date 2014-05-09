package cn.tt100.base.example.bean;

public class Page {
	/**
	 * 当前第几页
	 */
	public int pageNo;
	
	/**
	 * 总数
	 */
	public int total;
	
	/**
	 * 每页数量
	 */
	public int pageSize;
	
	/**
	 * 页数
	 */
	public int pages;

	
	public Page() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Page(int pageNo, int pageSize) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}
	
	
	public boolean isLastIndex(){
		return pageNo == pages || pages == 0;
	}


	@Override
	public String toString() {
		return "当前第"+pageNo+"/"+pages+"页,共"+total+"条数据";
	}
	
	
	
}
