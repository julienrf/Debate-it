package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class intended to help doing pagination
 * Highly inspired by the Yii CPagination class
 * @author julien
 *
 */
public class Pagination {
	private int itemCount;
	private String pageVar = "p";
	private int pageSize = 20;
	private int currentPage = 1;
	
	public Pagination(int itemCount) {
		this.itemCount = itemCount;
	}
	
	public Pagination(int itemCount, int currentPage) {
		this(itemCount);
		this.currentPage = currentPage;
	}
	
	public Pagination(int itemCount, int currentPage, int pageSize) {
		this(itemCount, currentPage);
		this.pageSize = pageSize;
	}
	
	public Pagination(int itemCount, int currentPage, int pageSize, String pageVar) {
		this(itemCount, currentPage, pageSize);
		this.pageVar = pageVar;
	}
	
	public int getPageCount() {
		return (itemCount + pageSize - 1) / pageSize;
	}
	
	public Map<String, Object> getParam(int page) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(pageVar, page);
		return map;
	}
	
	public int getCurrentOffset() {
		return (currentPage - 1) * pageSize;
	}
	
	public int getCurrentLimit() {
		return Math.min(currentPage * pageSize, itemCount);
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public String getPageVar() {
		return pageVar;
	}
}
