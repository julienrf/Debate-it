package utils;

import java.util.HashMap;
import java.util.Map;

import play.mvc.Scope;

/**
 * Class intended to help doing pagination
 * @author julien
 *
 */
public class Pagination {
	private Scope.Params params;
	private int itemCount;
	private String pageVar = "p";
	private int pageSize = 12;
	
	public Pagination(Scope.Params params, int itemCount) {
		this.params = params;
		this.itemCount = itemCount;
	}
	
	public Pagination(Scope.Params params, int itemCount, int pageSize) {
		this(params, itemCount);
		this.pageSize = pageSize;
	}
	
	public Pagination(Scope.Params params, int itemCount, int pageSize, String pageVar) {
		this(params, itemCount, pageSize);
		this.pageVar = pageVar;
	}
	
	public int getPageCount() {
		return (itemCount + pageSize - 1) / pageSize;
	}
	
	/**
	 * Return a map containing the parameters to use to get a given page
	 * @param page
	 * @return
	 */
	public Map<String, Object> getParam(int page) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(pageVar, page);
		return map;
	}
	
	public int getFrom() {
		return (getCurrentPage() - 1) * pageSize;
	}
	
	public int getTo() {
		return Math.min(getCurrentPage() * pageSize, itemCount);
	}
	
	public int getCurrentPage() {
		if (params._contains(pageVar)) {
			int currentPage = params.get(pageVar, Integer.class);
			if ((currentPage - 1) * pageSize >= itemCount) {
				return 1;
			} else {
				return currentPage;
			}
		} else {
			return 1;
		}
	}
	
	public String getPageVar() {
		return pageVar;
	}
}
