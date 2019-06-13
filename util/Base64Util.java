package com.eyaoshun.common.util;

import java.io.FileOutputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;

/**
 * @ClassName Base64Util
 * @Description 将base64字符串转成图片
 * @author wtao wangtao@eyaoshun.com
 * @date 2018年1月18日 下午7:17:22
 */

public class Base64Util {
	/**
	 * @Description TODO
	 * @param imgStr base64字符串
	 * @param path 图片路径
	 * @return boolean
	 */
	 
	public static boolean generateImage(String imgStr, String path) {
		if (imgStr == null)
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// 解密
			byte[] b = decoder.decodeBuffer(imgStr);
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

