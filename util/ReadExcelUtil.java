package com.eyaoshun.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @className:ReadExcelUtil
 * @Description:读取excel内容的工具类
 * @author xing.He Hexing@eyaoshun.com
 * 2018年4月26日 下午4:22:36
 */
public class ReadExcelUtil {
	private Logger logger = LoggerFactory.getLogger(ReadExcelUtil.class);
	private Workbook wb;  //工作表
	private Sheet sheet;  //工作空间
	private Row row;      //行
	
	
	/**
	 * 获取要上传文件的信息
	 * @param filepath
	 */
	public ReadExcelUtil(String filepath){
		if (filepath == null) {
			return ;
		}		
		String ext=filepath.substring(filepath.lastIndexOf("."));//截取最后一个点后面的字符串
		try {
			InputStream is= new FileInputStream(filepath);   //根据流读取excel文件
			if (".xls".equals(ext)) {  
				wb=new HSSFWorkbook(is);   //2003
			}else if (".xlsx".equals(ext)) {  
				wb=new XSSFWorkbook(is);   //2007+
			}else {
				wb=null;
			}	
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException",e);
		}catch (IOException e) {
			logger.error("IOException",e);
		}	
	}
	
	
	/**
	 * 读取Excel表格表头的内容
	 * @return
	 * @throws Exception
	 */
	public String[] readExcelTitle() throws Exception{
		if (wb==null) {
			throw new Exception("Workbook对象为空!");
		}
		sheet = wb.getSheetAt(1);
		row = sheet.getRow(0);
		//获取标题总列数
		int colNum = row.getPhysicalNumberOfCells();  //去除为空的列数
		String[] title =new String[colNum];
		for(int i=0;i<colNum;i++) {
			title[i]=row.getCell(i).getStringCellValue();
		}
		return title;
	}
	
	
	/**
	 * 正常读取Excel内容("第一行为表头,第二行开始为数据")
	 * @return
	 * @throws Exception
	 */
	public Map<Integer,Map<Integer,Object>> readExcelContent() throws Exception{
		if (wb==null) {
			throw new Exception("Workbook对象为空!");
		}
		Map<Integer, Map<Integer,Object>> content = new HashMap<Integer,Map<Integer, Object>>();
		//获取第一页
		sheet=wb.getSheetAt(0);  
		//得到总的行数
		int rowNum=sheet.getLastRowNum();
		row=sheet.getRow(0);
		int colNum =row.getPhysicalNumberOfCells();  //获取不为空的列的个数
		//第一行为表头的信息,正文内容从第二行开始
		for (int i = 1; i < rowNum; i++) {
			row=sheet.getRow(i);
			int j=0; 
			Map<Integer, Object> cellvalue =new HashMap<Integer,Object>();
			while (j<colNum) {
				Object obj = getCellFormatValue(row.getCell(j));
				cellvalue.put(j, obj);
				j++;
			}
			content.put(i, cellvalue);
		}
		return content;
	}
	
	
	/**
	 * 读取skuext表的信息
	 * @return
	 * @throws Exception
	 */
	public Map<Integer,Map<Integer,Object>> readExcelContentUpLoad()throws Exception{
		if (wb==null) {
			throw new Exception("workbook对象为空!");
		}
		Map<Integer, Map<Integer,Object>> content = new HashMap<Integer,Map<Integer, Object>>();
		//sheet1是商品分类表
		sheet=wb.getSheetAt(0);  
		//得到总的行数
		int rowNum=sheet.getLastRowNum();
		row=sheet.getRow(0);
		int colNum =row.getPhysicalNumberOfCells();  //获取不为空的列的个数
		//获取单元格的信息
		for (int i = 0; i <= rowNum; i++) {
			row=sheet.getRow(i);
			int j=0; 
			Map<Integer, Object> cellvalue =new HashMap<Integer,Object>();
			while (j<colNum) {
				Object obj = getCellFormatValue(row.getCell(j));
				cellvalue.put(j, obj);
				j++;
			}
			content.put(i, cellvalue);
			
		}
		return content;
	}
	
	
	
	/**
	 * 指定读取某一个工作空间的excel数据
	 * @param sheetIndex
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer,Object>> readExcelContent(int sheetIndex) throws Exception{
		if(wb==null){
			throw new Exception("Workbook对象为空！");
		}
		Map<Integer, Map<Integer,Object>> content = new HashMap<Integer, Map<Integer,Object>>();

		sheet = wb.getSheetAt(sheetIndex);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			int j = 0;
			Map<Integer,Object> cellValue = new HashMap<Integer, Object>();
			while (j < colNum) {
				Object obj = getCellFormatValue(row.getCell(j));
				cellValue.put(j, obj);
				j++;
			}
			content.put(i, cellValue);
		}
		return content;
	}
	
	/**
	 * 处理Excel单元格的值的类型
	 * @param cell
	 * @return
	 */
	private Object getCellFormatValue(Cell cell) {
		Object cellvalue = "";
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
			case Cell.CELL_TYPE_FORMULA: {
				cellvalue = String.valueOf(cell.getNumericCellValue()).trim();
				break;
			}
			case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString().trim();
				break;
			default:// 默认的Cell值
				cellvalue = "";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;
	}
	
	
	//==========================测试数据===================================//
	public static void main(String[] args)throws Exception {
		String filepath="D:\\0427分类模板.xlsx";
		ReadExcelUtil ed = new ReadExcelUtil(filepath);
		
		Map<Integer, Map<Integer,Object>> map = ed.readExcelContentUpLoad();
		System.out.println("获取Excel表格的内容:");
		for (int i =4; i < map.size(); i++) {
			System.out.println(map.get(i));
		}		
	}
}

