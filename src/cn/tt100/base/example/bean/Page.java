package cn.tt100.base.example.bean;

public class Page {
	/**
	 * ��ǰ�ڼ�ҳ
	 */
	public int pageNo;
	
	/**
	 * ����
	 */
	public int total;
	
	/**
	 * ÿҳ����
	 */
	public int pageSize;
	
	/**
	 * ҳ��
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
		return "��ǰ��"+pageNo+"/"+pages+"ҳ,��"+total+"������";
	}
	
	
	
}
