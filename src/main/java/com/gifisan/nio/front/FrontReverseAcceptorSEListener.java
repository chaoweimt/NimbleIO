package com.gifisan.nio.front;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.component.SEListenerAdapter;
import com.gifisan.nio.component.Session;

public class FrontReverseAcceptorSEListener extends SEListenerAdapter {

	private Logger				logger	= LoggerFactory.getLogger(FrontReverseAcceptorSEListener.class);

	private FrontRouterMapping	frontRouterMapping;

	public FrontReverseAcceptorSEListener(FrontRouterMapping routerProxy) {
		this.frontRouterMapping = routerProxy;
	}

	public void sessionOpened(Session session) {
		logger.info("负载服务器来自 " + session + " 已建立连接.");
		frontRouterMapping.addRouterSession(session);
	}

	public void sessionClosed(Session session) {
		logger.info("负载服务器来自 " + session + " 已断开连接.");
		frontRouterMapping.removeRouterSession(session);
	}
}
