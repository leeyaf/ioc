package org.leeyaf.iocmvc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class IocMvcListener implements ServletContextListener{
	public void contextInitialized(ServletContextEvent sce) {
		IocMvc.start(sce.getServletContext());
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		IocMvc.destory();
	}
}
