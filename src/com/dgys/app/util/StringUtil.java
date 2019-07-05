package com.dgys.app.util;

import java.io.UnsupportedEncodingException;

public class StringUtil {
	public static String convertToUTF8(String source) {

		if (source != null) {
			try {
				source = new String(source.getBytes("ISO-8859-1"), "UTF-8");

			} catch (UnsupportedEncodingException uee) {

			}
		}

		return source;
	}
}
