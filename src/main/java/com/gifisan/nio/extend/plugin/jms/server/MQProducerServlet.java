package com.gifisan.nio.extend.plugin.jms.server;

import java.io.OutputStream;

import com.gifisan.nio.common.ByteUtil;
import com.gifisan.nio.component.BufferedOutputStream;
import com.gifisan.nio.component.Session;
import com.gifisan.nio.component.protocol.nio.future.NIOReadFuture;
import com.gifisan.nio.extend.plugin.jms.Message;

public class MQProducerServlet extends MQServlet {

	public static final String	SERVICE_NAME	= MQProducerServlet.class.getSimpleName();

	public void doAccept(Session session, NIOReadFuture future, MQSessionAttachment attachment) throws Exception {

		MQContext context = getMQContext();

		if (future.hasOutputStream()) {

			OutputStream outputStream = future.getOutputStream();

			if (outputStream == null) {
				future.setOutputStream(new BufferedOutputStream(future.getStreamLength()));
				return;
			}
		}

		Message message = context.parse(future);

		context.offerMessage(message);

		future.write(ByteUtil.TRUE);

		session.flush(future);

	}

}
