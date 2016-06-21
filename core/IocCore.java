package com.gsteam.common.util.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gsteam.common.util.ioc.annotation.Autowired;
import com.gsteam.common.util.ioc.annotation.Bean;
import com.gsteam.common.util.ioc.annotation.Mapping;
import com.gsteam.common.util.ioc.support.ClassTemplate;

public class IocCore {
	private static String packageName="com.gsteam.common.util";
	private Map<Class<?>, Object> beanInstances=new HashMap<>();
	private Map<String, Object> servletInstances=new HashMap<>();
	private static IocCore THIS=new IocCore();
	
	public static void start(){
		THIS.init();
	}
	public static void destory(){
		// help GC		
		THIS.beanInstances=null;
		THIS.servletInstances=null;
	}
	
	private IocCore(){
	}
	
	/**
	 * 根据类型获取实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<?> type){
		return (T) THIS.beanInstances.get(type);
	}
	
	/**
	 * 根据mapping url 获取方法
	 */
	public static Method getMethod(String url){
		return (Method) THIS.servletInstances.get(url);
	}
	
	/**
	 * 初始化。扫描类，注入对象，创建url对于的method
	 */
	private void init(){
		try {
			List<Class<?>> beanClasses=new ClassTemplate(packageName) {
				@Override
				public boolean checkAddClass(Class<?> cls) {
					if (cls.isAnnotationPresent(Bean.class)) {	// 定义的bean
						return true;
					}else {										// 定义的注入，或者servlet
						Field[] fields=cls.getDeclaredFields();
						for (Field field : fields) {
							if (field.isAnnotationPresent(Autowired.class)) {
								return true;
							}
						}
						Method[] methods=cls.getDeclaredMethods();
						for (Method method : methods) {
							if (method.isAnnotationPresent(Mapping.class)) {
								return true;
							}
						}
					}
					return false;
				}
			}.getClassList();
			
			// 单例创建对象
			for (Class<?> beanClass : beanClasses) {
				Object instance=beanClass.newInstance();
				beanInstances.put(beanClass, instance);
			}
	
			// 把对象注入依赖
			for (Class<?> autowiredClass : beanClasses) {
				Field[] fields=autowiredClass.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Autowired.class)) {
						Class<?> fieldType=field.getType();
						Object value=beanInstances.get(fieldType);
						field.setAccessible(true);
						Object toWiredBean=beanInstances.get(autowiredClass);
						field.set(toWiredBean, value);
					}
				}
				Method[] methods=autowiredClass.getDeclaredMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(Mapping.class)) {
						Annotation annotation=method.getAnnotation(Mapping.class);
						Method annoMethod=annotation.getClass().getDeclaredMethod("path",new Class<?>[0]);
						String mappingUrl= (String) annoMethod.invoke(annotation, new Object[0]);
						servletInstances.put(mappingUrl, method);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
