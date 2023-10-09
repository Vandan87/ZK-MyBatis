package com.util;

import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * The utility class provides a way to obtain a MyBatis SqlSessionFactory.
 * The MyBatis configuration is typically stored in a ""SqlConfig.xml file.
 * 
 * @author VANDAN
 */
public class MyBatisUtil {

	public static SqlSessionFactory sqlSessionFactory;
	static {
		try {
			String resource = "SqlConfig.xml";
			// Load the configuration file as an input stream
			InputStream inputStream = Resources.getResourceAsStream(resource);
			// Build the SqlSessionFactory using the configuration
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (Exception e) {
			throw new ExceptionInInitializerError("Error initializing MyBatisUtil: " + e.getMessage());
		}
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		if (sqlSessionFactory == null) {
			throw new IllegalStateException("SqlSessionFactory is null. Check MyBatis configuration.");
		}
		return sqlSessionFactory;
	}

}