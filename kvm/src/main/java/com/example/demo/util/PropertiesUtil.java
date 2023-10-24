package com.example.demo.util;

import java.io.IOException;
import java.io.InputStream;

public class PropertiesUtil {
	
	private static java.util.Properties config = new java.util.Properties();

	static {
		try {
			InputStream inStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("vm.properties");
			config.load(inStream);
			inStream.close();
		} catch (IOException e) {
			System.out.println("No AreaPhone.properties defined error");
		}
	}
	
	public static void destroy() {
		config = null;
	}

	public static synchronized String get(String key) {
		 return config.getProperty(key);
	}
	
	public static synchronized int getInt(String key) {
		 return Integer.parseInt(config.getProperty(key));
	}
}