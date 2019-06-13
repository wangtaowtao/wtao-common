package com.eyaoshun.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/****
 * @className:KuaidiUtil
 * @Description:
 * @author xing.He Hexing@eyaoshun.com
 * 2018年11月30日 下午3:43:46
 */
public class KuaidiUtil {
	//商户id
	private String EBusinessID = "1394814";
	//商户密钥
	private String APPKey = "21edbf68-4617-482c-be6a-ce2a05128524";
	//请求地址
	private String ReqURL ="http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
	
	// -----------------------------接口请求参数----------------------------------------//
	// 请求系统级参数说明：
	// 	参数名称			 类型		 	说明 													必须要求
	// 	RequestData 	String 	请求内容需进行URL(utf-8)编码。请求内容JSON格式，须和DataType一致。	 R
	// 	EBusinessID 	String 	商户ID，请在我的服务页面查看。									 R
	// 	RequestType 	String 	请求指令类型：1008 										 R
	// 	DataSign 		String 	数据内容签名：把(请求内容(未编码)+AppKey)进行MD5加密，然后Base64编码，最后
	// 	进行URL(utf-8)编码。详细过程请查看Demo。 												 R
	// 	DataType 		String 	请求、返回数据类型：2-json； 									 R
	// 备注：R-必填（Required），O-可选（Optional），C-报文中该参数在一定条件下可选（Conditional）
	// ------------------------------接口请求参数----------------------------------------//
	
	//**************************************************************************************//
	//此工具类近支持以下查询：1.只有单号，通过单号反查快递公司信息，结合快递公司信息查询当前单号对应物流信息  //															//
	//********************2.有快递公司单号和快递公司对应的编码(编码由快递鸟提供)直接调用接口进行查询          //
	//**************************************************************************************//
		
	/**
	 * 快递单号+物流公司编码查询物流信息
	 * @param expCode 快递编号
	 * @param expNo   快递单号
	 * @return
	 * @throws Exception
	 */
	// 
	public String getOrderTraceByexpcodeNO(String expCode, String expNo) throws Exception {
		String requestData = "{'OrderCode':''," + "'ShipperCode':'" + expCode + "'," 
				+ "'LogisticCode':'" + expNo+ "'}";
		Map<String, String> params = new HashMap<String, String>();
		params.put("RequestData", urlEncoder(requestData, "UTF-8")); // 请求参数
		params.put("EBusinessID", EBusinessID); // 商户ID
		params.put("RequestType", "1002"); // 请求指令类型
		String dataSign = encrypt(requestData, APPKey, "UTF-8"); // base64进行编码
		params.put("DataSign", urlEncoder(dataSign, "UTF-8")); // url编码
		params.put("DataType", "2"); // 请求返回的数据类型
		String result = sendPost(ReqURL, params);
		return result;
	}
	
	/**
	 * 根据单号反查快递公司信息
	 * @param expNo
	 * @return
	 * @throws Exception
	 */
	public String getOrderTraceByexpNO(String expNo) throws Exception {
		String requestData= "{'LogisticCode':'" + expNo + "'}";
		Map<String, String> params = new HashMap<String, String>();
		params.put("RequestData", urlEncoder(requestData, "UTF-8")); // 请求参数
		params.put("EBusinessID", EBusinessID); // 商户ID
		params.put("RequestType", "2002"); // 请求指令类型
		String dataSign = encrypt(requestData, APPKey, "UTF-8"); // base64进行编码
		params.put("DataSign", urlEncoder(dataSign, "UTF-8")); // url编码
		params.put("DataType", "2"); // 请求返回的数据类型
		String result = sendPost(ReqURL, params);
		return result;
	}
	
	/**
	 * 解析单号查询结果
	 * @param result
	 * @return
	 */
	public static Map<String,String> praseShipper(String result) {
		com.alibaba.fastjson.JSONObject json = JSON.parseObject(result);
		String code = json.getString("LogisticCode");
		com.alibaba.fastjson.JSONArray jsonArray = json.getJSONArray("Shippers");
		com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(0);
		String name = (String)jsonObject.get("ShipperName");
		String shippercoed = (String)jsonObject.get("ShipperCode");
		
		Map<String,String> map = new HashMap<>();
		map.put("kddh", code);
		map.put("kdmc",name);
		map.put("kdbm", shippercoed);
		return map;
	}
	
	/**
	 * MD5加密
	 * 
	 * @param str
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private String MD5(String str, String charset) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5"); // 初始化MD5方法
		md.update(str.getBytes(charset)); // 处理数据
		byte[] result = md.digest(); // 完成散列码的计算
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			int val = result[i] & 0xff; // byte转换成int类型 (8进制转换成32进制)
			if (val <= 0xf) {
				sb.append("0"); // 补位
			}
			sb.append(Integer.toHexString(val)); // 将数值转换为16进制的字符串
		}
		return sb.toString().toLowerCase(); // 转换成小写
	}

	/**
	 * base64编码
	 * 
	 * @param str
	 *            内容
	 * @param charset
	 *            编码方式
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String base64(String str, String charset) throws UnsupportedEncodingException {
		String encoded = base64Encode(str.getBytes(charset));
		return encoded;
	}

	/**
	 * URL编码
	 * 
	 * @param str
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String urlEncoder(String str, String charset) throws UnsupportedEncodingException {
		String result = URLEncoder.encode(str, charset);
		return result;
	}

	/**
	 * 生成签名(将MD5加密后的数据再进行base64加密)
	 * 
	 * @param content
	 *            内容
	 * @param keyValue
	 *            APPKey
	 * @param charset
	 *            编码方式
	 * @return
	 * @throws Exception
	 */
	private String encrypt(String content, String keyValue, String charset) throws Exception {
		if (keyValue != null) {
			return base64(MD5(content + keyValue, charset), charset);
		}
		return base64(MD5(content, charset), charset);
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param params
	 *            请求的参数集合
	 * @return 远程资源的响应结果
	 */
	private String sendPost(String url, Map<String, String> params) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// POST方法
			conn.setRequestMethod("POST");
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 发送请求参数
			if (params != null) {
				StringBuilder param = new StringBuilder();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (param.length() > 0) {
						param.append("&");
					}
					param.append(entry.getKey());
					param.append("=");
					param.append(entry.getValue());
				}
				out.write(param.toString());
			}
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	public static String base64Encode(byte[] data) {
		StringBuffer sb = new StringBuffer();
		int len = data.length;
		int i = 0;
		int b1, b2, b3;
		while (i < len) {
			b1 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
				sb.append("==");
				break;
			}
			b2 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
				sb.append("=");
				break;
			}
			b3 = data[i++] & 0xff;
			sb.append(base64EncodeChars[b1 >>> 2]);
			sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
			sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
			sb.append(base64EncodeChars[b3 & 0x3f]);
		}
		return sb.toString();
	}
	
	//--------------测--------------试--------------接-------------口-----------------------//
	public static void main(String[] args) {
		KuaidiUtil api=new KuaidiUtil();
		/**快递公司+单号查询物流**/
		try {
			String result=api.getOrderTraceByexpcodeNO("UC", "900402218443");
			JSONObject jsonObject = JSONObject.fromObject(result); 
			String ShipperCode = jsonObject.getString("ShipperCode");
			String LogisticCode = jsonObject.getString("LogisticCode"); 
			JSONArray Traces = jsonObject.getJSONArray("Traces"); 
			System.out.println("快递名称:"+ShipperCode); 
			System.out.println("快递单号:"+LogisticCode); 
			for(int i = 0; i < Traces.size(); i++) { 
				JSONObject object = (JSONObject) Traces.get(i);
				String AcceptTime = object.getString("AcceptTime"); 
				String AcceptStation = object.getString("AcceptStation");
				System.out.println("时间："+AcceptTime+"\t"+AcceptStation); }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		/**单号识别**/
		try {
			String result=api.getOrderTraceByexpNO("75107098095974");
			System.err.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}

