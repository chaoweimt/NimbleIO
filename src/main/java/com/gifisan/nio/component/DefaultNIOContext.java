package com.gifisan.nio.component;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gifisan.nio.AbstractLifeCycle;
import com.gifisan.nio.Encoding;
import com.gifisan.nio.acceptor.ServerUDPEndPointFactory;
import com.gifisan.nio.common.LifeCycleUtil;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.LoggerUtil;
import com.gifisan.nio.common.SharedBundle;
import com.gifisan.nio.component.concurrent.ExecutorThreadPool;
import com.gifisan.nio.component.concurrent.ThreadPool;
import com.gifisan.nio.component.protocol.DefaultTCPProtocolDecoder;
import com.gifisan.nio.component.protocol.DefaultTCPProtocolEncoder;
import com.gifisan.nio.component.protocol.ProtocolDecoder;
import com.gifisan.nio.component.protocol.ProtocolEncoder;
import com.gifisan.nio.extend.configuration.ServerConfiguration;

public class DefaultNIOContext extends AbstractLifeCycle implements NIOContext {

	private Map<Object, Object>			attributes			= new HashMap<Object, Object>();
	private DatagramPacketAcceptor		datagramPacketAcceptor	;
	private Charset					encoding				= Encoding.DEFAULT;
	private IOEventHandleAdaptor			ioEventHandleAdaptor	;
	private SessionEventListenerWrapper	lastSessionEventListener	;
	private Logger						logger				= LoggerFactory
																.getLogger(DefaultNIOContext.class);
	private ProtocolDecoder				protocolDecoder		= new DefaultTCPProtocolDecoder();
	private ProtocolEncoder				protocolEncoder		= new DefaultTCPProtocolEncoder();
	private ReadFutureAcceptor			readFutureAcceptor		;
	private ServerConfiguration			serverConfiguration		;
	private SessionEventListenerWrapper	sessionEventListenerStub	;
	private SessionFactory				sessionFactory			= new SessionFactory();
	private IOService					tcpService			;
	private ThreadPool					threadPool			;
	private UDPEndPointFactory			udpEndPointFactory		;
	private IOService					udpService			;

	public DefaultNIOContext() {
		this.addLifeCycleListener(new NIOContextListener());
	}

	public void addSessionEventListener(SessionEventListener listener) {
		if (this.sessionEventListenerStub == null) {
			this.sessionEventListenerStub = new SessionEventListenerWrapper(listener);
			this.lastSessionEventListener = this.sessionEventListenerStub;
		} else {
			this.lastSessionEventListener.setNext(new SessionEventListenerWrapper(listener));
			this.lastSessionEventListener = this.lastSessionEventListener.nextListener();
		}
	}

	public void clearAttributes() {
		this.attributes.clear();
	}

	protected void doStart() throws Exception {

		SharedBundle bundle = SharedBundle.instance();

		if (ioEventHandleAdaptor == null) {
			throw new IllegalArgumentException("null ioEventHandle");
		}

		if (serverConfiguration == null) {
			this.serverConfiguration = loadServerConfiguration(bundle);
		}

		if (protocolEncoder == null) {
			protocolEncoder = new DefaultTCPProtocolEncoder();
		}

		if (protocolDecoder == null) {
			protocolDecoder = new DefaultTCPProtocolDecoder();
		}

		int SERVER_CORE_SIZE = serverConfiguration.getSERVER_CORE_SIZE();

		Charset encoding = serverConfiguration.getSERVER_ENCODING();

		Encoding.DEFAULT = encoding;

		this.encoding = Encoding.DEFAULT;
		this.threadPool = new ExecutorThreadPool("IOEvent-Executor", SERVER_CORE_SIZE);
		this.readFutureAcceptor = new ReadFutureDispatcher();
		this.udpEndPointFactory = new ServerUDPEndPointFactory();

		LoggerUtil.prettyNIOServerLog(logger,
				"======================================= 服务开始启动 =======================================");
		LoggerUtil.prettyNIOServerLog(logger, "项目编码     ：  { {} }", encoding);
		LoggerUtil.prettyNIOServerLog(logger, "监听端口(TCP)：  { {} }", serverConfiguration.getSERVER_TCP_PORT());
		if (serverConfiguration.getSERVER_UDP_PORT() != 0) {
			LoggerUtil.prettyNIOServerLog(logger, "监听端口(UDP)：  { {} }", serverConfiguration.getSERVER_UDP_PORT());
		}
		LoggerUtil.prettyNIOServerLog(logger, "CPU核心数    ：  { {} }", SERVER_CORE_SIZE);

		this.ioEventHandleAdaptor.start();
		this.ioEventHandleAdaptor.setContext(this);
		this.threadPool.start();
	}

	protected void doStop() throws Exception {

		LifeCycleUtil.stop(ioEventHandleAdaptor);

		LifeCycleUtil.stop(threadPool);
	}

	public Object getAttribute(Object key) {
		return this.attributes.get(key);
	}

	public Set<Object> getAttributeNames() {
		return this.attributes.keySet();
	}

	public DatagramPacketAcceptor getDatagramPacketAcceptor() {
		return datagramPacketAcceptor;
	}

	public Charset getEncoding() {
		return encoding;
	}

	public IOEventHandleAdaptor getIOEventHandleAdaptor() {
		return ioEventHandleAdaptor;
	}

	public ProtocolDecoder getProtocolDecoder() {
		return protocolDecoder;
	}

	public ProtocolEncoder getProtocolEncoder() {
		return protocolEncoder;
	}

	public ReadFutureAcceptor getReadFutureAcceptor() {
		return readFutureAcceptor;
	}

	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	public SessionEventListenerWrapper getSessionEventListenerStub() {
		return sessionEventListenerStub;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public IOService getTCPService() {
		return tcpService;
	}

	public ThreadPool getThreadPool() {
		return threadPool;
	}

	public UDPEndPointFactory getUDPEndPointFactory() {
		return udpEndPointFactory;
	}

	public IOService getUDPService() {
		return udpService;
	}

	private ServerConfiguration loadServerConfiguration(SharedBundle bundle) {

		ServerConfiguration configuration = new ServerConfiguration();

		String encoding = bundle.getProperty("SERVER.ENCODING", "GBK");

		configuration.setSERVER_CORE_SIZE(Runtime.getRuntime().availableProcessors());
		configuration.setSERVER_DEBUG(bundle.getBooleanProperty("SERVER.DEBUG"));
		configuration.setSERVER_HOST(bundle.getProperty("SERVER.HOST"));
		configuration.setSERVER_TCP_PORT(bundle.getIntegerProperty("SERVER.TCP_PORT"));
		configuration.setSERVER_UDP_PORT(bundle.getIntegerProperty("SERVER.UDP_PORT"));
		configuration.setSERVER_ENCODING(Charset.forName(encoding));

		return configuration;
	}

	public Object removeAttribute(Object key) {
		return this.attributes.remove(key);
	}

	public void setAttribute(Object key, Object value) {
		this.attributes.put(key, value);
	}

	public void setDatagramPacketAcceptor(DatagramPacketAcceptor datagramPacketAcceptor) {
		this.datagramPacketAcceptor = datagramPacketAcceptor;
	}

	public void setIOEventHandleAdaptor(IOEventHandleAdaptor ioEventHandleAdaptor) {
		this.ioEventHandleAdaptor = ioEventHandleAdaptor;
	}

	public void setProtocolDecoder(ProtocolDecoder protocolDecoder) {
		this.protocolDecoder = protocolDecoder;
	}

	public void setProtocolEncoder(ProtocolEncoder protocolEncoder) {
		this.protocolEncoder = protocolEncoder;
	}

	public void setServerConfiguration(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	public void setTCPService(IOService tcpService) {
		this.tcpService = tcpService;
	}

	public void setUDPEndPointFactory(UDPEndPointFactory udpEndPointFactory) {
		this.udpEndPointFactory = udpEndPointFactory;
	}

	public void setUDPService(IOService udpService) {
		this.udpService = udpService;
	}

}
