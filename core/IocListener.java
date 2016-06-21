package com.gsteam.common.util.ioc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class IocListener implements ServletContextListener{
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		IocCore.start();
		System.out.println("ioc environment has started");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		IocCore.destory();
		System.out.println("ioc environment has destoryed");
	}

}
