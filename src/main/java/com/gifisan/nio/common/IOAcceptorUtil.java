package com.gifisan.nio.common;

import com.gifisan.nio.acceptor.TCPAcceptor;
import com.gifisan.nio.component.DefaultNIOContext;
import com.gifisan.nio.component.IOEventHandleAdaptor;
import com.gifisan.nio.component.LoggerSEListener;
import com.gifisan.nio.component.NIOContext;
import com.gifisan.nio.component.SessionAliveSEListener;
import com.gifisan.nio.extend.configuration.ServerConfiguration;

public class IOAcceptorUtil {

	private static Logger		logger	= LoggerFactory.getLogger(IOAcceptorUtil.class);

	private static TCPAcceptor	acceptor;

	public static TCPAcceptor getTCPAcceptor(IOEventHandleAdaptor ioEventHandleAdaptor) {

		return getTCPAcceptor(ioEventHandleAdaptor, null);
	}

	public static TCPAcceptor getTCPAcceptor(IOEventHandleAdaptor ioEventHandleAdaptor,
			ServerConfiguration configuration) {

		if (acceptor == null) {

			try {

				acceptor = new TCPAcceptor();

				NIOContext context = new DefaultNIOContext();

				context.setServerConfiguration(configuration);

				context.setIOEventHandleAdaptor(ioEventHandleAdaptor);

				context.addSessionEventListener(new LoggerSEListener());

				context.addSessionEventListener(new SessionAliveSEListener());

				acceptor.setContext(context);

			} catch (Throwable e) {

				logger.error(e.getMessage(), e);

				acceptor.unbind();

				throw new RuntimeException(e);
			}
		}

		return acceptor;
	}

}
