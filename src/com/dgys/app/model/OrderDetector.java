package com.dgys.app.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.log4j.Logger;

public class OrderDetector {
	public static String factNo;
	private HashMap<String, IOrderParser> orderParsers = null;
	private PDDocument poDoc = null;
	private String orderDirStr;
	private File orderDir;
	private File successDir;
	private File failDir;
	private String curDayDir;
	private String flag = "F";
	private String successDirStr;
	private String failDirStr;
	private Logger loger = Logger.getLogger(getClass());

	public OrderDetector(String systemProperties) {
		
		try {
			
			Properties sysProper = new Properties();
			sysProper.load(ClassLoader.getSystemResourceAsStream(systemProperties));
			
			factNo = sysProper.getProperty("factNo");
			orderDirStr = sysProper.getProperty("orderDir");
			successDirStr = sysProper.getProperty("successDir");
			failDirStr = sysProper.getProperty("failDir");
			
			if(factNo == null || factNo.equals("")){
				throw new Exception("未配置屬性factNo");
			}
			
			if(successDirStr == null || successDirStr.equals("")){
				throw new Exception("未配置屬性successDir");
			}
			
			if(failDirStr == null || failDirStr.equals("")){
				throw new Exception("未配置屬性failDir");
			}
			
			initDir();
		} catch (Exception e) {
			loger.error(e.getMessage());
		}
	}
	
	public void start(){
		try {
			orderDir = new File(orderDirStr);

			File[] pdfOrders = orderDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".pdf");
				}
			});

			if (pdfOrders.length > 0){
				orderParsers = new HashMap<String, IOrderParser>();		
			}
			
			//循環處理每個pdf文檔
			for (int i = 0; i < pdfOrders.length; i++) {
				try {
					
					poDoc = PDDocument.load(pdfOrders[i]);

					PDFTextStripper textStripper = new PDFTextStripper();

					parseOrder(pdfOrders[i].getName(), textStripper.getText(poDoc));
					
					flag = "S";
				} catch (Exception ex) {
					loger.error(ex.getMessage() + "--" + "File:" +pdfOrders[i].getName());
					flag = "F";
					
				} finally {
					if (poDoc != null) {
						poDoc.close();
						moveFile(pdfOrders[i]);
					}
				}
			}
		} catch (Exception e) {
			loger.error(e.getMessage());
		}
	}

	private void parseOrder(String fileName, String orderText) throws Exception {
		IOrderParser orderParser = null;
		String tempOrderFile = "temp.txt";
		FileOutputStream fos = new FileOutputStream(tempOrderFile);

		fos.write(orderText.getBytes());
		fos.close();

		if (fileName.toUpperCase().startsWith("AMFIT")) {
			orderParser = orderParsers.get("amfit");
			if (orderParser == null) {
				orderParser = new AmfitOrderParser();
				orderParsers.put("amfit", orderParser);
			}
		}

		orderParser.ParseOrderText(tempOrderFile);
	}
	
	private void initDir()
	{
		//創建轉移文件夾
		successDir = new File(successDirStr);
		failDir = new File(failDirStr);
		
		if(!successDir.exists())
			successDir.mkdirs();
		
		if(!failDir.exists())
			failDir.mkdirs();
		
		Date currentDay = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		curDayDir = sdf.format(currentDay);
	}
	
	private void moveFile(File file)
	{
		File destFile = null;
		File destDir = null;
		String tmp = "\\" + curDayDir + "\\";
		
		if(flag.equals("S"))
			destDir = new File(successDir.getPath() + tmp);
		else if(flag.equals("F"))
			destDir = new File(failDir.getPath() + tmp);
		
		if(!destDir.exists())
			destDir.mkdirs();
		
		destFile = new File(destDir.getPath() + "\\" + file.getName());
		
		if(!file.renameTo(destFile))
			loger.error("移轉文件:" + file.getName() + "失敗!");
	}
}
