package com.gifisan.nio.extend;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gifisan.nio.AbstractLifeCycle;
import com.gifisan.nio.Encoding;
import com.gifisan.nio.common.InitializeUtil;
import com.gifisan.nio.common.LifeCycleUtil;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.LoggerUtil;
import com.gifisan.nio.common.SharedBundle;
import com.gifisan.nio.component.DatagramPacketAcceptor;
import com.gifisan.nio.component.NIOContext;
import com.gifisan.nio.component.SessionEventListener;
import com.gifisan.nio.extend.configuration.ApplicationConfiguration;
import com.gifisan.nio.extend.configuration.ApplicationConfigurationLoader;
import com.gifisan.nio.extend.configuration.FileSystemACLoader;
import com.gifisan.nio.extend.security.AuthorityLoginCenter;
import com.gifisan.nio.extend.security.RoleManager;
import com.gifisan.nio.extend.service.FutureAcceptor;
import com.gifisan.nio.extend.service.FutureAcceptorFilter;
import com.gifisan.nio.extend.service.FutureAcceptorService;
import com.gifisan.nio.extend.service.FutureAcceptorServiceFilter;
import com.gifisan.nio.extend.service.FutureAcceptorServiceLoader;

public class ApplicationContext extends AbstractLifeCycle {

	private static ApplicationContext	instance;

	public static ApplicationContext getInstance() {
		return instance;
	}

	private String							appLocalAddres;
	private Sequence						sequence			= new Sequence();
	private DynamicClassLoader				classLoader		= new DynamicClassLoader();
	private ApplicationConfiguration			configuration;
	private ApplicationConfigurationLoader		configurationLoader;
	private NIOContext						context;
	private Charset						encoding			= Encoding.DEFAULT;
	private FutureAcceptor					filterService;
	private Logger							logger			= LoggerFactory
																.getLogger(ApplicationContext.class);
	private LoginCenter						loginCenter		= new AuthorityLoginCenter();
	private List<FutureAcceptorFilter>			pluginFilters		= new ArrayList<FutureAcceptorFilter>();
	private Map<String, FutureAcceptorService>	pluginServlets		= new HashMap<String, FutureAcceptorService>();
	private RoleManager						roleManager		= new RoleManager();
	private FixedSessionFactory				sessionFactory		;
	private FutureAcceptorServiceLoader		acceptorServiceLoader;
	private Map<String, FutureAcceptorService>	services			= new LinkedHashMap<String, FutureAcceptorService>();
	private FutureAcceptorServiceFilter		lastServiceFilter	= null;

	protected void doStart() throws Exception {

		if (context == null) {
			throw new IllegalArgumentException("null nio context");
		}

		instance = this;

		SharedBundle bundle = SharedBundle.instance();

		if (configurationLoader == null) {
			configurationLoader = new FileSystemACLoader();
		}

		if (lastServiceFilter == null) {
			lastServiceFilter = new FutureAcceptorServiceFilter(classLoader);
		}

		filterService = new FutureAcceptor(this, classLoader, lastServiceFilter);

		this.configuration = configurationLoader.loadConfiguration(bundle);

		this.encoding = context.getEncoding();
		this.appLocalAddres = bundle.getClassPath() + "app/";

		LoggerUtil.prettyNIOServerLog(logger, "工作目录           ：{ {} }", appLocalAddres);

		LifeCycleUtil.start(filterService);

		this.roleManager.initialize(this, null);
		this.loginCenter.initialize(this, null);

		this.acceptorServiceLoader = filterService.getFutureAcceptorServiceLoader();
		this.acceptorServiceLoader.listen(services);
		this.sessionFactory = new FixedSessionFactory(context);
		this.context.setSessionFactory(sessionFactory);
	}

	public void addSessionEventListener(SessionEventListener listener) {
		context.addSessionEventListener(listener);
	}

	protected void doStop() throws Exception {
		LifeCycleUtil.stop(filterService);
		InitializeUtil.destroy(loginCenter, this, null);
		instance = null;
	}

	public String getAppLocalAddress() {
		return appLocalAddres;
	}

	public void setConfigurationLoader(ApplicationConfigurationLoader configurationLoader) {
		this.configurationLoader = configurationLoader;
	}

	public DynamicClassLoader getClassLoader() {
		return classLoader;
	}

	public ApplicationConfiguration getConfiguration() {
		return configuration;
	}

	public NIOContext getContext() {
		return context;
	}

	public DatagramPacketAcceptor getDatagramPacketAcceptor() {
		return context.getDatagramPacketAcceptor();
	}

	public Charset getEncoding() {
		return encoding;
	}

	public FutureAcceptor getFilterService() {
		return filterService;
	}

	public LoginCenter getLoginCenter() {
		return loginCenter;
	}

	public PluginContext getPluginContext(Class clazz) {

		PluginContext[] pluginContexts = filterService.getPluginContexts();

		for (PluginContext context : pluginContexts) {

			if (context == null) {
				continue;
			}

			if (context.getClass().isAssignableFrom(clazz)) {
				return context;
			}
		}
		return null;
	}

	public List<FutureAcceptorFilter> getPluginFilters() {
		return pluginFilters;
	}

	public Map<String, FutureAcceptorService> getPluginServlets() {
		return pluginServlets;
	}

	public RoleManager getRoleManager() {
		return roleManager;
	}

	public FixedSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public boolean redeploy() {

		ApplicationConfiguration configuration;
		try {
			configuration = configurationLoader.loadConfiguration(SharedBundle.instance());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			return false;
		}

		DynamicClassLoader classLoader = new DynamicClassLoader();

		boolean redeployed = filterService.redeploy(classLoader);

		if (redeployed) {

			this.configuration = configuration;

			this.classLoader = classLoader;
		}

		return redeployed;
	}

	public void setContext(NIOContext context) {
		this.context = context;
	}

	public void setDatagramPacketAcceptor(DatagramPacketAcceptor datagramPacketAcceptor) {
		context.setDatagramPacketAcceptor(datagramPacketAcceptor);
	}

	public void setLoginCenter(LoginCenter loginCenter) {

		if (loginCenter == null) {
			throw new IllegalArgumentException("null");
		}

		if (this.loginCenter.getClass() != AuthorityLoginCenter.class) {
			// FIXME 这里是否只能设置一次
			// throw new IllegalArgumentException("already setted");
		}

		this.loginCenter = loginCenter;
	}

	public void listen(String serviceName, FutureAcceptorService service) {

		if (isRunning()) {
			throw new IllegalStateException("listen before start");
		}

		this.services.put(serviceName, service);
	}

	public Sequence getSequence() {
		return sequence;
	}

	public void setLastServiceFilter(FutureAcceptorServiceFilter lastServiceFilter) {
		this.lastServiceFilter = lastServiceFilter;
	}
	
	
}
