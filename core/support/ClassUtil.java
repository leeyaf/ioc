package com.gsteam.common.util.ioc.support;


/**
 * Class操作工具类
 */
public class ClassUtil {
	
	/**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    
    /**
     * 加载类（将自动初始化）
     */
    public static Class<?> loadClass(String className) throws Exception {
        return loadClass(className, true);
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) throws Exception {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
        	throw e;
        }
        return cls;
    }
}
