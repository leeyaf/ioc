# iocmvc

原理介绍文章参见[这里](http://www.jianshu.com/p/b0383ef5007a)

## Installation

下载源码，使用maven构建

## 使用

在你的`web.xml`里插入下面这个监听器：

````xml
<listener>
  <listener-class>org.leeyaf.iocmvc.IocMvcListener</listener-class>
</listener>
````

注入一个`UserService`实例

````java
class Example{
  @Autowired
  private UserService userService;
}
````

注册一个Servlet：

````java
class Example{
  @Mapping(path="/comment/add")
  public void handleRequest(HttpServletRequest request,HttpServletResponse response){
    // do something here...
  }
}
````

使用mvc的前提：

你需要创建一个能拦截所有请求的Servlet，获取pathInfo，从mvc容器中拿到pathInfo对应的method，最后invoke即可。

例子：

````java
@WebServlet(urlPatterns="/*")
@MultipartConfig(maxFileSize=5242880)
public class RootServelt extends HttpServlet{
  private static final long serialVersionUID = 1L;
  
  @Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String pathInfo=request.getPathInfo();
    List<String> urls=IocMvc.getAllMapping();
    String matchUrl=""; // find url that eq pathInfo
    Method method=IocMvc.getMethod(matchUrl);
		Object instance=IocMvc.getInstance(method.getDeclaringClass());
    method.invoke(instance, request, response);
  }
}
````
