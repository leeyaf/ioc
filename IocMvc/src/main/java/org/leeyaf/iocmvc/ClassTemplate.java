package org.leeyaf.iocmvc;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 用于获取类的模板类
 */
public abstract class ClassTemplate {

    private final String packageName;
    private final ClassLoader classLoader;

    protected ClassTemplate(String packageName,ClassLoader classLoader) {
        this.packageName = packageName;
        this.classLoader=classLoader;
    }

    protected final List<Class<?>> getClassList() throws Exception{
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            // 从包名获取 URL 类型的资源
            Enumeration<URL> urls = classLoader.getResources(packageName.replace(".", "/"));
            // 遍历 URL 资源
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    // 获取协议名（分为 file 与 jar）
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        // 若在 class 目录中，则执行添加类操作
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classList, packagePath, packageName);
                    } 
                    /*else if (protocol.equals("jar")) {
                        // 若在 jar 包中，则解析 jar 包中的 entry
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            // 判断该 entry 是否为 class
                            if (jarEntryName.endsWith(".class")) {
                                // 获取类名
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                // 执行添加类操作
                                doAddClass(classList, className, packageName);
                            }
                        }
                    }*/
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return classList;
    }

    private void addClass(List<Class<?>> classList, String packagePath, String packageName) throws Exception {
        try {
            // 获取包名路径下的 class 文件或目录
            File[] files = new File(packagePath).listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
                }
            });
            // 遍历文件或目录
            for (File file : files) {
                String fileName = file.getName();
                // 判断是否为文件或目录
                if (file.isFile()) {
                    // 获取类名
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    if (StringUtils.isNotEmpty(packageName)) {
                        className = packageName + "." + className;
                    }
                    // 执行添加类操作
                    doAddClass(classList, className);
                } else {
                    // 获取子包
                    String subPackagePath = fileName;
                    if (StringUtils.isNotEmpty(packagePath)) {
                        subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    // 子包名
                    String subPackageName = fileName;
                    if (StringUtils.isNotEmpty(packageName)) {
                        subPackageName = packageName + "." + subPackageName;
                    }
                    // 递归调用
                    addClass(classList, subPackagePath, subPackageName);
                }
            }
        } catch (Exception e) {
        	throw e;
        }
    }

    private void doAddClass(List<Class<?>> classList, String className) throws Exception {
        // 加载类
    	Class<?> cls;
        try {
            cls = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
        	throw e;
        }
        // 判断是否可以添加类
        if (checkAddClass(cls)) {
            // 添加类
            classList.add(cls);
        }
    }
    
    /*private void doAddClass(List<Class<?>> classList, String className, String packageName) throws Exception{
        if (className.startsWith(packageName)) {
            doAddClass(classList, className);
        }
    }*/

    /**
     * 验证是否允许添加类
     * 
     * 使用设计模式：模板方法模式
     */
    protected abstract boolean checkAddClass(Class<?> cls);
}