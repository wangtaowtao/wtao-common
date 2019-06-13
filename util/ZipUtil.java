package com.eyaoshun.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {
	private static final int buffer=2048;
	//方式一
	public  void zipToFile(String path){
		String separator=File.separator; //定义分隔符
		int count=-1;
		String savepath="";
		File file=null;
		InputStream is=null;  
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;
		
		savepath=path.substring(0, path.lastIndexOf(separator));
		ZipFile zipFile=null;
		try {
			zipFile=new ZipFile(path,Charset.forName("GBK"));
			Enumeration<?> entries = zipFile.entries();  //获取压缩包下文件的集合
			while (entries.hasMoreElements()) {
				byte buf[]=new byte[buffer];
				ZipEntry entry=(ZipEntry) entries.nextElement();
				String filename=entry.getName();
				boolean ismkdir=false;
				if ((filename.lastIndexOf("/"))!=-1) {
					ismkdir=true;
				}
				filename=savepath+separator+filename;
				if (entry.isDirectory()) {  //如果是文件夹则先创建文件夹
					file=new File(filename);
					file.mkdirs();
					continue;
				}
				file=new File(filename);
				if (!file.exists()) { //如果是目录,先创建目录
					if (ismkdir) {
						new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs();
					}
				}
				file.createNewFile();  //创建文件
				is=zipFile.getInputStream(entry);
				fos=new FileOutputStream(file);
				bos=new BufferedOutputStream(fos, buffer);
				while ((count=is.read(buf))>-1) {
					bos.write(buf, 0, count);
				}
				bos.flush();
				bos.close();
				fos.close();
				is.close();
			}		
			zipFile.close();		
		}catch (IOException ioe) {
			ioe.printStackTrace();
		}finally {
			try {
				if (bos!=null) {
					bos.close();
				}
				if (fos!=null) {
					fos.close();
				}
				if (is!=null) {
					is.close();
				}
				if (zipFile!=null) {
					zipFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}