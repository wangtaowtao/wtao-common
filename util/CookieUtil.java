package com.eyaoshun.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * 
 * @ClassName: CookieUtil
 * @Description:  cookie操作工具类
 * @author ping.lv lvping@eyaoshun.com
 * @date 2017年11月27日 下午4:06:34
 */
public class CookieUtil {

	private static final Logger logger = LoggerFactory.getLogger(CookieUtil.class);
	/**
	 * 设置cookie
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(path);
		if (maxAge > 0) cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		setCookie(response, name, value, "/", maxAge);
	}
	public static void setCookie(HttpServletResponse response, String name, String value) {
		setCookie(response, name, value, "/", 3600);
	}
	public static void setCookie(HttpServletResponse response, String name) {
		setCookie(response, name, "", "/", 3600);
	}
	//wtao
	public static void setCookie(HttpServletResponse response, String name,String value,String domain ) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(3600*24*365);
		cookie.setDomain(domain);
		response.addCookie(cookie);
	}
	//wtao
	public static void deleCookie(HttpServletResponse response, String name,String domain) {
		Cookie cookie = new Cookie(name, "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		cookie.setDomain(domain);
		response.addCookie(cookie);
	}

	/**
	 * 获取cookie
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String name) {
		String value = null;
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					value = cookie.getValue();
				}
			}
		}
		return value;
	}

	/**
	 * 删除cookie
	 * @param response发DVD发v反对v发
	 * @param name
	 * @return
	 */
	public static void removeCookie(HttpServletResponse response, String name) {
		setCookie(response, name, "", "/", 0);
	}
	/**
	 * ljq cart=====================================================================
	 * 获取指定cookies的值 findCookieByName
	 * @param request
	 * @param name
	 * @return String
	 */
	public static String findCookieByName(HttpServletRequest request,String name) {
		Cookie[] cookies = request.getCookies();
		if(null == cookies || cookies.length == 0) return null;
		String string = null;
		try {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				String cname = cookie.getName();
				if (!StringUtils.isBlank(cname) && cname.equals(name)) {
					string = cookie.getValue();
				}
			}
		} catch (Exception ex) {
			logger.error("获取Cookies发生异常！", ex);
		}
		return string;
	}
	/**
	 * 设置cookie
	 * 
	 * @param response
	 * @param name
	 *            cookie名字
	 * @param value
	 *            cookie值
	 * @param maxAge
	 *            cookie生命周期 以秒为单位
	 */
	public static void addCookie(HttpServletResponse response, String name,
			String value, int maxAge, String domain) {
		try {
			Cookie cookie = new Cookie(name, value);
			if (maxAge > 0)
				cookie.setMaxAge(maxAge);
			cookie.setDomain(domain);
			cookie.setPath("/");
			response.addCookie(cookie);
		} catch (Exception ex) {
			logger.error("创建Cookies发生异常！", ex);
		}
	}
	/**
	 * 获取指定cookies的值 findCookieByName
	 * 
	 * @param request
	 * @param name
	 * @return String
	 */
	public static void addCookie(HttpServletRequest request,HttpServletResponse response,String name,
			String value,long quantity, int maxAge, String domain) {
		try {
			String cartInfo = CookieUtil.findCookieByName(request,name);
			String[] subInfo;
			String[] newCartInfo = new String[]{};
			Cookie cookie = null;
			if(!StringUtils.isEmpty(cartInfo)){
				String[] cartInfoArr = cartInfo.split(",");
				newCartInfo = new String[cartInfoArr.length];
				//		/		info: skuid|quantity
				int i = 0;
				boolean f = true;
				for(String info : cartInfoArr){
					subInfo = info.split("\\|");
					if(subInfo[0].equals(value)){						
						newCartInfo[i] = subInfo[0] + "|" + quantity;
						f = false;
					}else
					{
						newCartInfo[i] = subInfo[0] + "|" + subInfo[1];
					}
					i++;
				}
				cartInfo = StringUtils.join(newCartInfo,",");
				if(f)
				{
					cartInfo = cartInfo + "," + value+"|"+quantity;
				}
				cookie = new Cookie(name, cartInfo);

			}else
			{
				cookie = new Cookie(name, value+"|"+quantity);
			}
			if (maxAge > 0)
				cookie.setMaxAge(maxAge);
			cookie.setDomain(domain);
			cookie.setPath("/");
			clearCookie(request, response, name,domain);
			response.addCookie(cookie);
		} catch (Exception ex) {
			logger.error("创建Cookies发生异常！", ex);
		}
	}
	public static boolean hasCookie(String cartInfo,long skuid)
	{
		boolean flag = false;
		String[] i;
		if(!StringUtils.isEmpty(cartInfo)){
			String[] cartInfoArr = cartInfo.split(",");
			//info: skuid|quantity
			for(String info : cartInfoArr){
				i = info.split("|");
				if(i[0].equals(new Long(skuid).toString())){
					flag = true;
					break;
				}
			}	
		}
		return flag;
	}
	/**
	 * 清空Cookie操作 clearCookie
	 * 
	 * @param request
	 * @param response
	 * @return boolean
	 * @author JIANG FEI Jun 19, 2014 10:12:17 AM
	 */
	public static boolean clearCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String domain) {
		boolean bool = false;
		Cookie[] cookies = request.getCookies();
		if(null == cookies || cookies.length == 0) return bool;
		try {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = new Cookie(name, null);
				cookie.setMaxAge(0);
				cookie.setPath("/");// 根据你创建cookie的路径进行填写
				cookie.setDomain(domain);
				response.addCookie(cookie);
				bool = true;
			}
		} catch (Exception ex) {
			logger.error("清空Cookies发生异常！", ex);
		}
		return bool;
	}
	/**
	 * 清空Cookie操作 clearCookie
	 * 
	 * @param request
	 * @param response
	 * @return boolean
	 * @author JIANG FEI Jun 19, 2014 10:12:17 AM
	 */
	public static boolean clearCookie(HttpServletRequest request,
			HttpServletResponse response, String name,String value, String domain) {
		boolean bool = false;
		try {
			String cartInfo = CookieUtil.findCookieByName(request,name);
			String[] subInfo;
			String[] newCartInfo = new String[]{};
			Cookie cookie = null;
			if(!StringUtils.isEmpty(cartInfo)){
				String[] cartInfoArr = cartInfo.split(",");
				newCartInfo = new String[cartInfoArr.length];
				//info: skuid|quantity
				int i = 0;
				boolean f = true;
				for(String info : cartInfoArr){
					subInfo = info.split("\\|");
					if(subInfo[0].equals(value)){						
						continue;
					}else
					{
						newCartInfo[i] = subInfo[0] + "|" + subInfo[1];
					}
					i++;
				}
				cartInfo = StringUtils.join(newCartInfo,",");
				cookie = new Cookie(name, cartInfo);

			}
			cookie.setDomain(domain);
			cookie.setPath("/");
			clearCookie(request, response, name,domain);
			response.addCookie(cookie);
		} catch (Exception ex) {
			logger.error("创建Cookies发生异常！", ex);
		}
		return bool;
	}



}
