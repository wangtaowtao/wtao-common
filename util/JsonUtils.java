package com.eyaoshun.common.util;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @className:JsonUtils
 * @Description:json工具类
 * @author xing.He Hexing@eyaoshun.com
 * 2018年8月29日 下午3:33:47
 */
public class JsonUtils {
	/**
	 * 定义jackson对象
	 */
	private static final ObjectMapper Mapper = new ObjectMapper();
	
	/**
	 * 将对象转换成json字符串
	 * @param data
	 * @return
	 */
	public static String objetcToJson(Object data) {
		try {
			String string=Mapper.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将json结果集转换成对象
	 * @param jsonData
	 * @param beanType
	 * @return
	 */
	public static <T> T jsonToObject(String jsonData,Class<T>beanType) {
		try {
			T t = Mapper.readValue(jsonData, beanType);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T>List<T> jsonToList(String jsonData,Class<T>beanType){
		JavaType javaType=Mapper.getTypeFactory().constructParametricType(List.class, beanType);
		try {
			List<T> list= Mapper.readValue(jsonData, javaType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

