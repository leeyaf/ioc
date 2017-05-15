package org.leeyaf.iocmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注入被<code>@Bean</code>修饰的实体,只能注解字段.用例如下:<br/>
 * 
 * class IndexAction{<br/>
 * &nbsp;&nbsp;<code>@Autowired</code><br/>
 * &nbsp;&nbsp;private UserService userService;<br/>
 * }
 * @author MikeD
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

}
