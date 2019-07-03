package com.dgys.app.model;

public class OrderParseMain {
	public static String factNo = "";
	public static String customNo = "";

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				OrderDetector orderDetector = new OrderDetector("config.properties");
				orderDetector.start();
				System.exit(0);
			} else {
				if (args.length < 3) {
					throw new Exception("傳入參數錯誤!");
				}

				// 解析指定的文件
				factNo = args[0];
				customNo = args[1];

				OrderParseMain parser = new OrderParseMain();
				parser.parseSingleFile(args[2]);
				System.exit(0);
			}
		} catch (Exception e) {
			System.exit(1);
		}
	}

	public void parseSingleFile(String filePath) throws Exception {
		OrderDetector orderDetector = new OrderDetector();
		orderDetector.parseSingleFile(filePath);
	}

}
