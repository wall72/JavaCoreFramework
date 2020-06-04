package jcf.query.loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.transaction.TransactionConfig;
import com.ibatis.sqlmap.engine.transaction.external.ExternalTransactionConfig;

@SuppressWarnings("deprecation")
public class AsyncRefreshableSqlMapClientFactoryBean implements FactoryBean<SqlMapClient>, InitializingBean,  DisposableBean {

	private static final Logger logger = LoggerFactory
			.getLogger(AsyncRefreshableSqlMapClientFactoryBean.class);

	/*
	 * target wrapper
	 */
	private AtomicReference<SqlMapClientFactoryBean> targetReference;

	private Resource[] configLocations;

	private Resource[] mappingLocations;
//	private Resource[] mappingLocationsToWatch;
	private List<Resource> mappingLocationListToWatchList;

	private Properties sqlMapClientProperties;

	private DataSource dataSource;

	private boolean useTransactionAwareDataSource = true;

	private Class<ExternalTransactionConfig> transactionConfigClass = ExternalTransactionConfig.class;

	private Properties transactionConfigProperties;

	private LobHandler lobHandler;

//	private SqlMapClient sqlMapClient;


	public AsyncRefreshableSqlMapClientFactoryBean() {
		this.transactionConfigProperties = new Properties();
		this.transactionConfigProperties.setProperty("SetAutoCommitAllowed", "false");
	}

	/**
	 * Set the location of the iBATIS SqlMapClient config file.
	 * A typical value is "WEB-INF/sql-map-config.xml".
	 * @see #setConfigLocations
	 */
	public void setConfigLocation(Resource configLocation) {
		this.configLocations = (configLocation != null ? new Resource[] {configLocation} : null);
	}

	/**
	 * Set multiple locations of iBATIS SqlMapClient config files that
	 * are going to be merged into one unified configuration at runtime.
	 */
	public void setConfigLocations(Resource[] configLocations) {
		this.configLocations = configLocations;
	}

	/**
	 * Set locations of iBATIS sql-map mapping files that are going to be
	 * merged into the SqlMapClient configuration at runtime.
	 * <p>This is an alternative to specifying "&lt;sqlMap&gt;" entries
	 * in a sql-map-client config file. This property being based on Spring's
	 * resource abstraction also allows for specifying resource patterns here:
	 * e.g. "/myApp/*-map.xml".
	 * <p>Note that this feature requires iBATIS 2.3.2; it will not work
	 * with any previous iBATIS version.
	 */
	public void setMappingLocations(Resource[] mappingLocations) {
		this.mappingLocations = mappingLocations;
	}

	/**
	 * Set optional properties to be passed into the SqlMapClientBuilder, as
	 * alternative to a <code>&lt;properties&gt;</code> tag in the sql-map-config.xml
	 * file. Will be used to resolve placeholders in the config file.
	 * @see #setConfigLocation
	 * @see com.ibatis.sqlmap.client.SqlMapClientBuilder#buildSqlMapClient(java.io.InputStream, java.util.Properties)
	 */
	public void setSqlMapClientProperties(Properties sqlMapClientProperties) {
		this.sqlMapClientProperties = sqlMapClientProperties;
	}

	/**
	 * Set the DataSource to be used by iBATIS SQL Maps. This will be passed to the
	 * SqlMapClient as part of a TransactionConfig instance.
	 * <p>If specified, this will override corresponding settings in the SqlMapClient
	 * properties. Usually, you will specify DataSource and transaction configuration
	 * <i>either</i> here <i>or</i> in SqlMapClient properties.
	 * <p>Specifying a DataSource for the SqlMapClient rather than for each individual
	 * DAO allows for lazy loading, for example when using PaginatedList results.
	 * <p>With a DataSource passed in here, you don't need to specify one for each DAO.
	 * Passing the SqlMapClient to the DAOs is enough, as it already carries a DataSource.
	 * Thus, it's recommended to specify the DataSource at this central location only.
	 * <p>Thanks to Brandon Goodin from the iBATIS team for the hint on how to make
	 * this work with Spring's integration strategy!
	 * @see #setTransactionConfigClass
	 * @see #setTransactionConfigProperties
	 * @see com.ibatis.sqlmap.client.SqlMapClient#getDataSource
	 * @see SqlMapClientTemplate#setDataSource
	 * @see SqlMapClientTemplate#queryForPaginatedList
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Set whether to use a transaction-aware DataSource for the SqlMapClient,
	 * i.e. whether to automatically wrap the passed-in DataSource with Spring's
	 * TransactionAwareDataSourceProxy.
	 * <p>Default is "true": When the SqlMapClient performs direct database operations
	 * outside of Spring's SqlMapClientTemplate (for example, lazy loading or direct
	 * SqlMapClient access), it will still participate in active Spring-managed
	 * transactions.
	 * <p>As a further effect, using a transaction-aware DataSource will apply
	 * remaining transaction timeouts to all created JDBC Statements. This means
	 * that all operations performed by the SqlMapClient will automatically
	 * participate in Spring-managed transaction timeouts.
	 * <p>Turn this flag off to get raw DataSource handling, without Spring transaction
	 * checks. Operations on Spring's SqlMapClientTemplate will still detect
	 * Spring-managed transactions, but lazy loading or direct SqlMapClient access won't.
	 * @see #setDataSource
	 * @see org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
	 * @see SqlMapClientTemplate
	 * @see com.ibatis.sqlmap.client.SqlMapClient
	 */
	public void setUseTransactionAwareDataSource(boolean useTransactionAwareDataSource) {
		this.useTransactionAwareDataSource = useTransactionAwareDataSource;
	}

	/**
	 * Set the iBATIS TransactionConfig class to use. Default is
	 * <code>com.ibatis.sqlmap.engine.transaction.external.ExternalTransactionConfig</code>.
	 * <p>Will only get applied when using a Spring-managed DataSource.
	 * An instance of this class will get populated with the given DataSource
	 * and initialized with the given properties.
	 * <p>The default ExternalTransactionConfig is appropriate if there is
	 * external transaction management that the SqlMapClient should participate
	 * in: be it Spring transaction management, EJB CMT or plain JTA. This
	 * should be the typical scenario. If there is no active transaction,
	 * SqlMapClient operations will execute SQL statements non-transactionally.
	 * <p>JdbcTransactionConfig or JtaTransactionConfig is only necessary
	 * when using the iBATIS SqlMapTransactionManager API instead of external
	 * transactions. If there is no explicit transaction, SqlMapClient operations
	 * will automatically start a transaction for their own scope (in contrast
	 * to the external transaction mode, see above).
	 * <p><b>It is strongly recommended to use iBATIS SQL Maps with Spring
	 * transaction management (or EJB CMT).</b> In this case, the default
	 * ExternalTransactionConfig is fine. Lazy loading and SQL Maps operations
	 * without explicit transaction demarcation will execute non-transactionally.
	 * <p>Even with Spring transaction management, it might be desirable to
	 * specify JdbcTransactionConfig: This will still participate in existing
	 * Spring-managed transactions, but lazy loading and operations without
	 * explicit transaction demaration will execute in their own auto-started
	 * transactions. However, this is usually not necessary.
	 * @see #setDataSource
	 * @see #setTransactionConfigProperties
	 * @see com.ibatis.sqlmap.engine.transaction.TransactionConfig
	 * @see com.ibatis.sqlmap.engine.transaction.external.ExternalTransactionConfig
	 * @see com.ibatis.sqlmap.engine.transaction.jdbc.JdbcTransactionConfig
	 * @see com.ibatis.sqlmap.engine.transaction.jta.JtaTransactionConfig
	 * @see com.ibatis.sqlmap.client.SqlMapTransactionManager
	 	 */
	public void setTransactionConfigClass(Class<ExternalTransactionConfig> transactionConfigClass) {
		if (transactionConfigClass == null || !TransactionConfig.class.isAssignableFrom(transactionConfigClass)) {
			throw new IllegalArgumentException("Invalid transactionConfigClass: does not implement " +
					"com.ibatis.sqlmap.engine.transaction.TransactionConfig");
		}
		this.transactionConfigClass = transactionConfigClass;
	}

	/**
	 * Set properties to be passed to the TransactionConfig instance used
	 * by this SqlMapClient. Supported properties depend on the concrete
	 * TransactionConfig implementation used:
	 * <p><ul>
	 * <li><b>ExternalTransactionConfig</b> supports "DefaultAutoCommit"
	 * (default: false) and "SetAutoCommitAllowed" (default: true).
	 * Note that Spring uses SetAutoCommitAllowed = false as default,
	 * in contrast to the iBATIS default, to always keep the original
	 * autoCommit value as provided by the connection pool.
	 * <li><b>JdbcTransactionConfig</b> does not supported any properties.
	 * <li><b>JtaTransactionConfig</b> supports "UserTransaction"
	 * (no default), specifying the JNDI location of the JTA UserTransaction
	 * (usually "java:comp/UserTransaction").
	 * </ul>
	 * @see com.ibatis.sqlmap.engine.transaction.TransactionConfig#initialize
	 * @see com.ibatis.sqlmap.engine.transaction.external.ExternalTransactionConfig
	 * @see com.ibatis.sqlmap.engine.transaction.jdbc.JdbcTransactionConfig
	 * @see com.ibatis.sqlmap.engine.transaction.jta.JtaTransactionConfig
	 */
	public void setTransactionConfigProperties(Properties transactionConfigProperties) {
		this.transactionConfigProperties = transactionConfigProperties;
	}

	/**
	 * Set the LobHandler to be used by the SqlMapClient.
	 * Will be exposed at config time for TypeHandler implementations.
	 * @see #getConfigTimeLobHandler
	 * @see com.ibatis.sqlmap.engine.type.TypeHandler
	 * @see org.springframework.orm.ibatis.support.ClobStringTypeHandler
	 * @see org.springframework.orm.ibatis.support.BlobByteArrayTypeHandler
	 * @see org.springframework.orm.ibatis.support.BlobSerializableTypeHandler
	 */
	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}



	/*
	 * 파일 감시
	 */
	private SqlMapClient proxy;

	private int interval;

	private Timer timer;
	private TimerTask task;

	/**
	 * 파일 감시 쓰레드가 실행중인지 여부.
	 */
	private boolean running = false;



//	public void setSqlMapClientFactoryBean(SqlMapClientFactoryBean target) {
//		this.target = target;
//	}

	public void afterPropertiesSet() throws Exception {
		targetReference = new AtomicReference<SqlMapClientFactoryBean>(getNewTarget());
		setRefreshable();
	}

	private SqlMapClientFactoryBean getNewTarget() throws Exception {
		logger.info("refreshing sqlMapClient.");

		scanSqlMapFiles();

		SqlMapClientFactoryBean sqlMapClientFactoryBean = new SqlMapClientFactoryBean();
//		sqlMapClientFactoryBean.setConfigLocation(configLocation);

		if (logger.isDebugEnabled()) {
			for (Resource configLocation : configLocations) {
				logger.debug("config : {}", configLocation);
			}
			for (Resource mappingLocation : mappingLocations) {
				logger.debug("mapping : {}", mappingLocation);
			}
		}
		sqlMapClientFactoryBean.setConfigLocations(configLocations);
		sqlMapClientFactoryBean.setDataSource(dataSource);
		sqlMapClientFactoryBean.setLobHandler(lobHandler);
		sqlMapClientFactoryBean.setMappingLocations(mappingLocations);
		sqlMapClientFactoryBean.setSqlMapClientProperties(sqlMapClientProperties);
		sqlMapClientFactoryBean.setTransactionConfigClass(transactionConfigClass);
		sqlMapClientFactoryBean.setTransactionConfigProperties(transactionConfigProperties);
		sqlMapClientFactoryBean.setUseTransactionAwareDataSource(useTransactionAwareDataSource);

		sqlMapClientFactoryBean.afterPropertiesSet();

		logger.info("refreshed sqlMapClient.");

		return sqlMapClientFactoryBean;
	}

	private void scanSqlMapFiles() {
		mappingLocationListToWatchList = extractMappingLocations(configLocations);

		if (this.mappingLocations != null) {
			mappingLocationListToWatchList.addAll(Arrays.asList(this.mappingLocations));
		}
	}

	public SqlMapClient getObject() throws Exception {
		return proxy;
	}

	public Class<?> getObjectType() {
		return (proxy != null ? proxy.getClass() : SqlMapClient.class);
	}

	public boolean isSingleton() {
		return true;
	}

	private SqlMapClient getParentObject() {
		return targetReference.get().getObject();
	}

	private void setRefreshable() {
		proxy = (SqlMapClient) Proxy.newProxyInstance(SqlMapClient.class
				.getClassLoader(), new Class[] { SqlMapClient.class,
				ExtendedSqlMapClient.class }, new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

				return method.invoke(getParentObject(), args);
			}

		});

		task = new TimerTask() {

			private Map<Resource, Long> map = new HashMap<Resource, Long>();

			public void run() {

				if (isModified()) {
					try {
						targetReference.set(getNewTarget());
					}
					catch (Exception e) {
						logger.error("caught exception", e);
					}
				}

			}

			private boolean isModified() {
				boolean retVal = false;

				for (int i = 0; i < configLocations.length; i++) {
					Resource configLocation = configLocations[i];
					retVal |= findModifiedResource(configLocation);
				}

//				if (mappingLocationsToWatch != null) {
//					for (int i = 0; i < mappingLocationsToWatch.length; i++) {
				for (Resource mappingLocation : mappingLocationListToWatchList) {
//						Resource mappingLocation = mappingLocationsToWatch[i];
						retVal |= findModifiedResource(mappingLocation);
					}
//				}

				return retVal;
			}

			private boolean findModifiedResource(Resource resource) {
				boolean retVal = false;
				List<String> modifiedResources = new ArrayList<String>();

				try {
					long modified = resource.lastModified();

					if (map.containsKey(resource)) {
						long lastModified = (map.get(resource));

						if (lastModified != modified) {
							map.put(resource, new Long(modified));
							modifiedResources.add(resource.getDescription());
							retVal = true;
						}

					}
					else {
						map.put(resource, new Long(modified));
					}
				}
				catch (IOException e) {
					logger.error("caught exception", e);
				}

				if (retVal) {
					logger.info("modified files : {}", modifiedResources);
				}
				return retVal;
			}

		};

		timer = new Timer(true);
		resetInterval();


		scanSqlMapFiles();
	}

	private List<Resource> extractMappingLocations(Resource[] configLocations) {
		List<Resource> mappingLocationList = new ArrayList<Resource>();
		SqlMapExtractingSqlMapConfigParser configParser = new SqlMapExtractingSqlMapConfigParser();
		for (int i = 0; i < configLocations.length; i++) {
			try {
				InputStream is = configLocations[i].getInputStream();
				mappingLocationList.addAll(configParser.parse(is));
			}
			catch (IOException ex) {
				logger.warn("Failed to parse config resource: {}", configLocations[i], ex.getCause());
			}
		}
		return mappingLocationList;
	}


	public void setCheckInterval(int ms) {
		interval = ms;

		if (timer != null) {
			resetInterval();
		}
	}

	private void resetInterval() {
		if (running) {
			timer.cancel();
			running = false;
		}
		if (interval > 0) {
			timer.schedule(task, 0, interval);
			running = true;
		}
	}

	public void destroy() throws Exception {
		timer.cancel();
	}

}
