package com.eyaoshun.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtil {
	private Log log = LogFactory.getLog(getClass());
	private static final FileUtil instance = new FileUtil();
 
	/**
	 * instance
	 * @return
	 */
	public static FileUtil getInstance() {
		return instance;
	}


	public boolean mkdir(String dir) {
		File file = new File(dir);
		if (file.exists() && file.isDirectory())
			return true;
		return file.mkdirs();
	}


	public boolean exists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}


	public void mv(String srcPath, String destPath) {
		File src = new File(srcPath);
		File dest = new File(destPath);
		if (!src.exists())
			return;

		src.renameTo(dest);
	}

	public File getFile(String path) {
		return new File(path);
	}


	public boolean delete(String filePath) {
		File file = new File(filePath);
		if (file.exists())
			return file.delete();
		else
			return true;
	}


	public Writer getFileWriter(String path) throws Exception {
		File file = new File(path);
		if (!file.getParentFile().exists())
			//return null;
			file.getParentFile().mkdirs();
		try {
			return new FileWriter(path);
		} catch (IOException e) {
			throw new Exception(FileUtil.class + "#getFileWriter IOException", e);
		}
	}

	public Writer getFileWriter(String path,String encoding) throws Exception {
		File file = new File(path);
		if (!file.getParentFile().exists())
		{
			file.getParentFile().mkdirs();
		}
		try 
		{			
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),encoding));
		} catch (IOException e) {
			throw new Exception(FileUtil.class + "#getFileWriter IOException", e);
		}
	}	


	private boolean cp(InputStream fins, File destine) {
		try {
			if (fins == null)
				return false;
			destine.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(destine);
			byte[] buf = new byte[1024];
			int readLen;
			while ((readLen = fins.read(buf, 0, buf.length)) > 0) {
				fos.write(buf, 0, readLen);
			}
			fos.flush();
			fos.close();
			fins.close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}


	public boolean cp(File src, File destine) {
		try {
			if (!src.exists() || src.getCanonicalPath().equals(destine.getCanonicalPath()))
				return false;
			FileInputStream fins = new FileInputStream(src);
			cp(fins, destine);
			destine.setLastModified(src.lastModified());
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	public boolean cp(String srcPath, String desPath) {
		File src = new File(srcPath);
		File destine = new File(desPath);
		return cp(src, destine);
	}

	public boolean writeFile(String fileName,String Content)
	{
		try
		{
			FileWriter fw=new FileWriter(fileName);//true ׷��
			fw.write(Content);
			fw.close();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	

	public boolean writeFile(String fileName, String Content, String encode){
		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(fileName), encode);
			out.write(Content);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

	public String ReadFile(String fileName)
	{
		try
		{
			FileReader fr=new FileReader(fileName);
			StringBuffer sb=new StringBuffer();
			BufferedReader br = new BufferedReader (fr);
			String s;
             while ((s = br.readLine() )!=null) {
            	 sb.append(s+"\r\n");
             }
			return sb.toString();
		}
		catch(Exception e)
		{
			return "";
		}
	}
	

	public void rsync(String scriptPath,String sendFile){
		//System.out.println(scriptPath);
		if(scriptPath == null){
			return;
		}
		String scriptFile="rsyncscript.sh";
		//String scriptPath=Convert.sTrim(pro.getProperty("rsync.path"));
		if("".equals(scriptPath)){
			return;
		}
		String sp = scriptPath+scriptFile;
		File file = new File(sp);
		if(!file.exists()){
			return;
		}
		String ss = "sh " + sp + " " + sendFile.substring(9);
		log.info("send path="+ss);
		Process process = null;
		String _ss1 = "";
		int exitValue = 0;
		try {
			process = Runtime.getRuntime().exec(ss);
			InputStreamReader ir1 = new InputStreamReader(process.getErrorStream());
			LineNumberReader input1 = new LineNumberReader(ir1);
			String line1;
			while ((line1 = input1.readLine()) != null) {
				_ss1 = _ss1 + line1 + "\r\n";
			}
			exitValue = process.exitValue();
		} catch (IOException e) {
			log.error("", e);
		} catch(IllegalThreadStateException e1){
			log.error("", e1);
		} finally {
			process.destroy();
			log.info("err=" + ss + "," + _ss1);
			log.info("process.exitValue()=" + ss + "," + exitValue);
			if(exitValue!=0 || !"".equals(_ss1)){  //���ʹ�õ�һ���ű�����ʧ�ܣ��໻����һ����
				_ss1 = "";
				exitValue = 0;
				try {
					process = Runtime.getRuntime().exec(ss.replace(scriptFile, "rsyncscript_bak.sh"));
					InputStreamReader ir1 = new InputStreamReader(process.getErrorStream());
					LineNumberReader input1 = new LineNumberReader(ir1);
					String line1;
					while ((line1 = input1.readLine()) != null) {
						_ss1 = _ss1 + line1 + "\r\n";
					}
					exitValue = process.exitValue();
				} catch (IOException e) {
					log.error("", e);
				} catch(IllegalThreadStateException e1){
					log.error("", e1);
				}finally {
					process.destroy();
					log.info("err_bak=" + ss + "," + _ss1);
					log.info("process.exitValue()_bak=" + ss + "," + exitValue);
				}
			}
		}
	}
}
