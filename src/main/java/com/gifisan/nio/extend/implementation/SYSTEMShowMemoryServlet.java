package com.gifisan.nio.extend.implementation;

import com.gifisan.nio.component.Session;
import com.gifisan.nio.component.protocol.nio.future.NIOReadFuture;
import com.gifisan.nio.extend.service.NIOFutureAcceptorService;

public class SYSTEMShowMemoryServlet extends NIOFutureAcceptorService{
	
	public static final String SERVICE_NAME = SYSTEMShowMemoryServlet.class.getSimpleName();

	protected void doAccept(Session session, NIOReadFuture future) throws Exception {
		
		int M = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		StringBuilder builder = new StringBuilder();
		builder.append("总内存：");
		builder.append(runtime.totalMemory()/M);
		builder.append("M;\n已占用内存：");
		builder.append((runtime.totalMemory() - runtime.freeMemory()) / M);
		builder.append("M;\n空闲内存：");
		builder.append(runtime.freeMemory() / M);
		builder.append("M;\n总内存：");
		builder.append(runtime.maxMemory()/M + "M;");
		
		future.write("服务器内存使用情况：\n");
		future.write(builder.toString());
		
		session.flush(future);
		
	}
	
}
