package org.leeyaf.iocmvc;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 用于获取类的模板类
 */
public abstract class ClassTemplate {
	private static Logger logger=Logger.getLogger(ClassTemplate.class);
    private final ClassLoader classLoader;

    protected ClassTemplate(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }

    protected final List<Class<?>> getClassList() throws Exception{
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
        	URL url=classLoader.getResource("");
            if (url != null) {
                String protocol = url.getProtocol();
                if (protocol.equals("file")) {
                    // 若在 class 目录中，则执行添加类操作
                    String path = url.getPath();
                    path=path.substring(0, path.length()-1);
                    logger.debug("root path: "+path);
                    addClass(classList, path,"");
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return classList;
    }

    private void addClass(List<Class<?>> classList, String currentPath,String currentPackage) throws Exception {
        try {
        	if(StringUtils.isBlank(currentPath)){
        		throw new Exception("currentPath is empty");
        	}
        	logger.debug("current path: "+currentPath+"; current package: "+currentPackage);
            // 获取该路径下的 class文件和目录
            File[] files = new File(currentPath).listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return (file.isFile() && file.getName().endsWith(".class") && file.getName().indexOf("$")<0) || file.isDirectory();
                }
            });
            // 遍历文件和目录
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile()) {	// 文件
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    doAddClass(classList, currentPackage+"."+className);
                } else {	// 目录
                    String subPath = currentPath +"/"+ fileName;
                    String subPackage=null;
                    if(currentPackage.length()<1){
                    	subPackage=fileName;
                    }else{
                    	subPackage=currentPackage+"."+fileName;
                    }
                    addClass(classList, subPath,subPackage);
                }
            }
        } catch (Exception e) {
        	throw e;
        }
    }

    private void doAddClass(List<Class<?>> classList, String className) throws Exception {
    	logger.debug("check class: "+className);
    	Class<?> cls;
        try {
            cls = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
        	throw e;
        }
        if (checkAddClass(cls)) {
            classList.add(cls);
        }
    }

    /**
     * 验证是否允许添加类
     * 
     * 使用设计模式：模板方法模式
     */
    protected abstract boolean checkAddClass(Class<?> cls);
}