package com.eyaoshun.common.util;


import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: Paginator
 * @Description: 分页实体类
 * @author ping.lv lvping@eyaoshun.com
 * @date 2017年11月27日 下午4:10:15
 */
public class Paginator {

	private long total = 1;			// 总记录数
	private int page = 1;				// 当前页数
	private long totalPage = 1;			// 总页数
	private int rows = 20;				// 每页记录数
	private int step = 5;				// 最多显示分页页码数
	private String param = "page";		// 分页参数名称，用于支持一个页面多个分页功能
	private String url = "";			// 项目路径
	private String query = "";			// 当前页所有参数

	public Paginator() {

	}

	public Paginator(long total, int page, int rows, HttpServletRequest request) {
		setTotal(total);
		setPage(page);
		setRows(rows);
		setUrl(request.getRequestURI());
		setQuery(request.getQueryString());
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
		this.initTotalPage();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		// 设置个最大记录数，限制单页记录过多
		if (rows > 10000) {
			rows = 10000;
		}
		this.rows = rows;
		this.initTotalPage();
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * 初始化分页信息
	 */
	public void initTotalPage() {
		totalPage = (total % rows) == 0 ? (total / rows) : ((total / rows) + 1);
		if (page > totalPage) {
			page = (int) totalPage;
		}
		if (page < 1) {
			page = 1;
		}
	}
	
	/**
	 * 生成简单的分页页面内容
	 * @return
	 */
	public String getHtml() {
		// 根据request获取当前url，包括参数，如果有已存在名称未paramname的参数，剔除掉，后面会追加新的参数
		//String contextPath = request.getContextPath();
		//String requestURI = request.getRequestURI();
		//String url = contextPath + requestURI;
		//String url = request.getRequestURI();
		//String query = request.getQueryString();
		if (query != null) {
			String params = "";
			String[] querys = query.split("&");
			for (int i = 0; i < querys.length; i++) {
				if (querys[i].startsWith(param))
					continue;
				if (params.equals(""))
					params = querys[i];
				else
					params += "&" + querys[i];
			}
			if (!params.equals(""))
				url += "?" + params;
		}
		
		if (url.contains("?")) {
			url = url + "&" + param + "=";
		}else
		{
			url = url + "?" + param + "=";
		}
		int pageCount = (int) Math.ceil((double) total / rows);// 求总页数
		if (pageCount <= 1) {
			return "";
		}
		if (page > pageCount) {
			page = pageCount;// 如果分页变量大总页数，则将分页变量设计为总页数
		}
		if (page <= 0) {
			page = 1;// 如果分页变量小于１,则将分页变量设为１
		}
		String firstString;
		if((page-rows)>1)
			firstString = "<li><a class='first a1 data-pjax external_link' href='" + url + "1'>首页</a></li>";
		else
			firstString = "";
		String preString;
		if (page > 1 && page <= pageCount) {
			if (url.contains("?")) {
				preString = "<li><a class='a1 data-pjax external_link' href='" + url +(page - 1) +"'>上一页</a></li>";
			}else {
				preString = "<li><a class='a1 data-pjax external_link' href='" + url +(page - 1) +"'>上一页</a></li>";
			}
		}else
		{
			preString = "<li class='active'><a>上一页</a><li>";
		}
		//页码描述信息
		String pageDesc = "总计 <span id=\"totalRecords\">" + total + "</span>条记录，分为<span id=\"totalPages\">" + pageCount + "页</span>";
		
		//输入框
		String intput = "<div class='input-append'><input id='pagekeydown' type='text' name='page' value='" + page + "' class='pageinput' style='width:30px;' onkeydown = \"javascript:"
				+ "if(event.keyCode==13){"
				+ "location.href='" + url + "'+this.value;}\"/><button class='btn' onclick = \"javascript:var input = document.getElementById('pagekeydown');location.href='"
				+ url + "'+input.value;\">GO</button></div>";
		// 结果html
		String pages = "<div class=\"page pagination\"><div class=\"pull-left\">" + pageDesc + "</div><div class=\"pull-right\">"+intput+"<ul>"+firstString+preString;

		String textList;
		// 如果总页数大于要显示的个数，则拼接显示
		if (pageCount > step) {
			// 显示分页码
			int listBegin = (page - (int) Math.floor((double) step / 2));// 从第几页开始显示分页信息
			if (listBegin < 1) {
				listBegin = 1;
			}
			// 显示第1页
			if (listBegin >= 2) {
				pages = pages.concat("<li><a class='a1 data-pjax external_link' href='" + url + "1'>1</a></li>");
			}
			// 当前页数右侧还有未显示页码时
			if (totalPage - page >= page - listBegin) {
				for (int i = listBegin; i < (listBegin + step); i++) {
					if (i != page) {
						pages = pages.concat("<li><a class='a1 data-pjax external_link' href='" + url + i + "'>" + i + "</a></li>");
					} else {
						pages = pages.concat("<li class='active'><a>" + i + "</a></li>");
					}
				}
				// 显示最后1页
				if (listBegin + step <= totalPage) {
					pages = pages.concat("<li><a class='a1 data-pjax external_link' href='" + url  + pageCount + "'>" + pageCount + "</a></li>");
				}
			} else { // 显示最后剩余的几个页码
				for (int i = (pageCount - step) + 1; i <= pageCount; i++) {
					if (i != page) {
						pages = pages.concat("<li><a class='a1 data-pjax external_link' href='" + url  + i  + "'>" + i  + "</a></li>");
					} else {
						pages = pages.concat("<li class='active'><a>" + i  + "</a></li>");
					}
				}
			}
		} else { // 总页数小于等于step时，直接显示
			for (int i = 1; i <= pageCount; i++) {
				if (i != page) {
					pages = pages.concat("<li><a class='a1 data-pjax external_link' href='" + url  + i  + "'>" + i  + "</a></li>");
				} else {
					pages = pages.concat("<li class='active'><a>" + i  + "</a></li>");
				}
			}
		}
		String nextString;
		if (page < pageCount) {	
			nextString =  "<li><a class='a1 data-pjax external_link' href='" + url  + (page + 1) + "'>下一页</a></li>";
        }else
        	nextString = "<li class='active'><a>下一页</a></li>";
		pages = pages.concat(nextString);
		
		String endString;
		if(page<(pageCount-rows))
			endString = "<li><a class='end a1 data-pjax external_link' href='"+ url  + pageCount + "'>尾页</a></li>";
		else
			endString = "";
		
		pages = pages.concat(endString);
		pages = pages.concat("</ul></div></div>");
		return pages;
	}

}
