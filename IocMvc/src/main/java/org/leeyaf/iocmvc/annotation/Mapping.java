package org.leeyaf.iocmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使一个方法可以处理HTTP请求.用例如下:<br/>
 * 
 * class IndexAction{<br/>
 * &nbsp;&nbsp;<code>@Mapping(path="/index.html")</code><br/>
 * &nbsp;&nbsp;public void index(HttpServletRequest request,HttpServletResponse response) throws Exception{<br/>
 * &nbsp;&nbsp;}<br/>
 * }
 * 
 * @author MikeD
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
	public String path() default "";
	
	public enum METHOD {GET,POST}
	
	METHOD method() default METHOD.GET;
}
