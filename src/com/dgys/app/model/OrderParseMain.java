package com.dgys.app.model;

import java.io.FileNotFoundException;
import java.io.IOException;

public class OrderParseMain {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		OrderDetector orderDetector = new OrderDetector("config.properties");
		orderDetector.start();
		System.exit(0);
	}

}
