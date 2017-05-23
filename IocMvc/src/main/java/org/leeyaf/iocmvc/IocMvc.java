package org.leeyaf.iocmvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.leeyaf.iocmvc.annotation.Autowired;
import org.leeyaf.iocmvc.annotation.Mapping;

public class IocMvc {
	private static Logger logger=Logger.getLogger(IocMvc.class);
	private Map<Class<?>, Object> instanceMap=new HashMap<>();
	private Map<String, Method> mappingMap=new HashMap<>();
	
	private static IocMvc THIS=new IocMvc();
	private IocMvc(){}
	
	public static void start(ServletContext context){
		THIS.init(context);
	}
	public static void destory(){
		logger.debug("destory IOCMVC...");
		// help GC
		THIS.instanceMap.clear();
		THIS.mappingMap.clear();
		THIS.instanceMap=null;
		THIS.mappingMap=null;
	}
	
	/**
	 * 根据类型获取实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<?> type){
		return (T) THIS.instanceMap.get(type);
	}
	
	/**
	 * 根据mapping url 获取方法
	 */
	public static Method getMethod(String url){
		return (Method) THIS.mappingMap.get(url);
	}
	
	public static List<String> getAllMapping(){
		List<String> mappingList=new ArrayList<String>();
		Iterator<String> iterator=THIS.mappingMap.keySet().iterator();
		while (iterator.hasNext()) {
			mappingList.add(iterator.next());
		}
		return mappingList;
	}
	
	/**
	 * 初始化。扫描类，注入对象，创建url对应的method
	 */
	private void init(ServletContext context){
		try {
			long time1=Calendar.getInstance().getTimeInMillis();
			logger.debug("starting IOCMVC...");
			
			List<Class<?>> allClasses=new ClassTemplate(context.getClassLoader()) {
				@Override
				public boolean checkAddClass(Class<?> c) {
					return true;
				}
			}.getClassList();
			logger.debug("total class size: "+allClasses.size());
			
			List<Field> needAutowiredFields=new ArrayList<>(); 
			for (Class<?> clazz : allClasses) {
				Field[] fields=clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Autowired.class)) {
						if(!instanceMap.containsKey(field.getType())){
							instanceMap.put(field.getType(), field.getType().newInstance());
						}
						if(!instanceMap.containsKey(clazz)){
							instanceMap.put(clazz, clazz.newInstance());
						}
						needAutowiredFields.add(field);
					}
				}
				Method[] methods=clazz.getDeclaredMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(Mapping.class)) {
						Annotation annotation=method.getAnnotation(Mapping.class);
						Method annoMethod=annotation.getClass().getDeclaredMethod("path",new Class<?>[0]);
						String mappingUrl= (String) annoMethod.invoke(annotation, new Object[0]);
						if(!instanceMap.containsKey(clazz)){
							instanceMap.put(clazz, clazz.newInstance());
						}
						mappingMap.put(mappingUrl, method);
					}
				}
			}
			logger.debug("instance size: "+instanceMap.size());
			logger.debug("autowired size: "+needAutowiredFields.size());
			logger.debug("mapping size: "+mappingMap.size());
			
			for (Field field : needAutowiredFields) {
				field.setAccessible(true);
				field.set(instanceMap.get(field.getDeclaringClass()), instanceMap.get(field.getType()));
			}
			long time2=Calendar.getInstance().getTimeInMillis();
			logger.debug("total time used: "+(time2-time1)+" ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
