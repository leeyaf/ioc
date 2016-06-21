package com.gsteam.common.util.ioc;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServelt extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url=req.getPathInfo();
		try {
			Method method=IocCore.getMethod(url);
			Object instance=IocCore.getInstance(method.getDeclaringClass());
			method.invoke(instance, req,resp);
		} catch (Exception e) {
			System.out.println("The request ["+url+"] is not mapped! Error: "+e.getMessage());
		}
	}
}
