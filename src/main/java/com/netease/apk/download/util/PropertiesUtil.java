package com.netease.apk.download.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * properties文件读取
 * 
 * @author
 */
public class PropertiesUtil {
	private static Log LOG = LogFactory.getLog(PropertiesUtil.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> getProperties(String filePath) {
		Properties properties = new Properties();
		try {
			Reader reader = new InputStreamReader(new FileInputStream(new File(filePath)), "utf8");
			properties.load(reader);
			return new HashMap(properties);
		} catch (FileNotFoundException e) {
			LOG.error("error in read file", e);
		} catch (IOException e) {
			LOG.error("error in IOException", e);
		}
		return null;
	}

	public static Map<String, String> deleteProperties(Map<String, String> pro, String[] save) {
		Map<String, String> map = new HashMap<String, String>();
		for (String key : pro.keySet()) {
			for (int i = 0; i < save.length; i++) {
				if (key.startsWith(save[i])) {
					map.put(key, pro.get(key));
				}
			}
		}
		return map;
	}

	public static void main(String[] args) {
		Map<String, String> map = getProperties("./setting");
		for (String k : map.keySet()) {
			System.out.println(k + ":" + map.get(k));
		}
	}
}
