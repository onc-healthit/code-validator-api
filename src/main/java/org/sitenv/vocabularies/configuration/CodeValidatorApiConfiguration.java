package org.sitenv.vocabularies.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.sitenv.vocabularies.loader.VocabularyLoadRunner;
import org.sitenv.vocabularies.loader.VocabularyLoaderFactory;
import org.sitenv.vocabularies.validation.NodeValidatorFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by Brian on 2/5/2016.
 */
@Configuration
@EnableAsync
@ComponentScan("org.sitenv.vocabularies")
@EnableJpaRepositories("org.sitenv.vocabularies.validation.repositories")
@PropertySource("classpath:CodeValidator.properties")
public class CodeValidatorApiConfiguration implements AsyncConfigurer {
	private static final Logger logger = Logger.getLogger(CodeValidatorApiConfiguration.class);
	private static final String HSQL_JDBC_URL_TEMPLATE = "jdbc:h2:mem:inmemdb;DB_CLOSE_DELAY=-1;MULTI_THREADED=1;CACHE_SIZE=1048576";


	@Value("classpath:schema.sql")
	private Resource HSQL_SCHEMA_SCRIPT;

	@Autowired
	private Environment environment;

	@Value("${executor.maxPoolSize}")
	private int maxPoolSize;

	@Value("${executor.corePoolSize}")
	private int corePoolSize;

	@Value("${executor.queueCapacity}")
	private int queueCapacity;

	@Bean(name = "taskExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
		e.setThreadGroupName("Spring-group-");
		e.setThreadNamePrefix("Spring-thread-");
		e.setMaxPoolSize(maxPoolSize);
		e.setCorePoolSize(corePoolSize);
		e.setQueueCapacity(queueCapacity);
		e.initialize();
		return e;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public ServiceLocatorFactoryBean vocabularyValidatorFactoryServiceLocatorFactoryBean() {
		ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
		bean.setServiceLocatorInterface(NodeValidatorFactory.class);
		return bean;
	}

	@Bean
	public NodeValidatorFactory vocabularyValidatorFactory() {
		return (NodeValidatorFactory) vocabularyValidatorFactoryServiceLocatorFactoryBean().getObject();
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(false);
		vendorAdapter.setShowSql(true);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("org.sitenv.vocabularies.validation.entities");
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.hbm2ddl.auto", "none");
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		jpaProperties.put("hibernate.format_sql", "true");
		jpaProperties.put("hibernate.show_sql", "false");
		jpaProperties.put("hibernate.connection.pool_size", "80");
		factory.setDataSource(dataSource());
		factory.setJpaProperties(jpaProperties);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}

	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}

	@Autowired
	@Bean
	public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
		final DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator());
		return initializer;
	}

	private DatabasePopulator databasePopulator() {
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(HSQL_SCHEMA_SCRIPT);
		return populator;
	}

	@Value("${dataSource.initialSize}")
	private int initialSize;

	@Value("${dataSource.minIdle}")
	private int minIdle;

	@Value("${dataSource.maxActive}")
	private int maxActive;

	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl(HSQL_JDBC_URL_TEMPLATE);
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setInitialSize(initialSize);
		ds.setMinIdle(minIdle);
		ds.setMaxTotal(maxActive); // DBCP2 maxTotal: The maximum number of active connections that can be allocated from this pool at the same time, or negative for no limit.
		ds.setDriverClassName("org.h2.Driver");
		return ds;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setLocalOverride(true);
		return propertySourcesPlaceholderConfigurer;
	}

	@Bean
	public ServiceLocatorFactoryBean vocabularyLoaderFactoryServiceLocatorFactoryBean() {
		ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
		bean.setServiceLocatorInterface(VocabularyLoaderFactory.class);
		return bean;
	}

	@Bean
	public VocabularyLoaderFactory vocabularyLoaderFactory() {
		return (VocabularyLoaderFactory) vocabularyLoaderFactoryServiceLocatorFactoryBean().getObject();
	}

	@Autowired
	@Bean
	VocabularyLoadRunner vocabularyLoadRunner(final Environment environment,
											  final VocabularyLoaderFactory vocabularyLoaderFactory, final DataSourceInitializer dataSourceInitializer,
											  final DataSource dataSource) {
		// VocabularyLoadRunner vocabularyLoadRunner(final Environment
		// environment, final VocabularyLoaderFactory vocabularyLoaderFactory,
		// final DataSourceInitializer dataSourceInitializer, final DataSource
		// dataSource){
		VocabularyLoadRunner vocabularyLoadRunner = null;
		String localCodeRepositoryDir = environment.getProperty("vocabulary.localCodeRepositoryDir");
		String localValueSetRepositoryDir = environment.getProperty("vocabulary.localValueSetRepositoryDir");
		vocabularyLoadRunner = new VocabularyLoadRunner();
		logger.info("LOADING VOCABULARY DATABASES FROM THE FOLLOWING RESOURCES: VALUESETS - "
				+ localValueSetRepositoryDir + " CODES - " + localCodeRepositoryDir);
		vocabularyLoadRunner.setCodeDirectory(localCodeRepositoryDir);
		vocabularyLoadRunner.setValueSetDirectory(localValueSetRepositoryDir);
		vocabularyLoadRunner.setDataSource(dataSource);
		vocabularyLoadRunner.setVocabularyLoaderFactory(vocabularyLoaderFactory);

		BasicConfigurator.configure();

		return vocabularyLoadRunner;
	}

	/*
	 * Following open source method is commented and re-factored it to support
	 * MU2 document validation.
	 */

	// ------------------------- INTERNAL CODE CHANGE START --------------------------
	/*
	 * @Bean public List<ConfiguredExpression>
	 * vocabularyValidationConfigurations(ValidationConfigurationLoader
	 * configurationLoader){ return
	 * configurationLoader.getConfigurations().getExpressions(); }
	 */
	// ------------------------- INTERNAL CODE CHANGE END --------------------------

	// ------------------------- INTERNAL CODE CHANGE START --------------------------
	// @Bean
	// public DocumentBuilder documentBuilder() throws
	// ParserConfigurationException {
	// DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	// domFactory.setNamespaceAware(true);
	// return domFactory.newDocumentBuilder();
	// }

	@Bean
	public DocumentBuilderFactory documentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		return domFactory;
	}

	// ------------------------- INTERNAL CODE CHANGE END  ------------------

	@Bean
	public XPathFactory xPathFactory() {
		return XPathFactory.newInstance();
	}

	@Autowired
	@Bean
	public ValidationConfigurationLoader validationConfigurationLoader(final Environment environment) {
		ValidationConfigurationLoader validationConfigurationLoader = new ValidationConfigurationLoader();
		validationConfigurationLoader
				.setValidationConfigurationFilePath(environment.getProperty("referenceccda.configFile"));
		validationConfigurationLoader.setUnmarshaller(castorMarshaller());
		return validationConfigurationLoader;
	}

	@Bean
	public Jaxb2Marshaller castorMarshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setPackagesToScan("org.sitenv.vocabularies.configuration");
		Map<String, Object> map = new HashMap<>();
		map.put("jaxb.formatted.output", true);
		jaxb2Marshaller.setMarshallerProperties(map);
		return jaxb2Marshaller;
	}

	/*
	 * Following set of code is added for enhancing open source code base to
	 * support MU2 document validation.
	 */
	// ------------------------- INTERNAL CODE CHANGE START --------------------------

	@Bean
	public List<ConfiguredExpression> vocabularyValidationConfigurations(
			ValidationConfigurationLoader configurationLoader) {

		List<ConfiguredExpression> mu3Expressions = new ArrayList<ConfiguredExpression>();
		List<ConfiguredExpression> expressions = configurationLoader.getConfigurations().getExpressions();
		for (ConfiguredExpression configuredExpression : expressions) {
			List<ConfiguredValidator> configuredValidators = configuredExpression.getConfiguredValidators();
			for (ConfiguredValidator configuredValidator : configuredValidators) {
				if (StringUtils.isEmpty(configuredValidator.getScope())) {
					mu3Expressions.add(configuredExpression);
					break;
				}
			}
		}
		logger.info("Number of MU3 expressions:" + mu3Expressions.size());
		return mu3Expressions;
	}

	@Bean
	public List<ConfiguredExpression> vocabularyValidationConfigurationsForMu2(
			ValidationConfigurationLoader configurationLoader) {

		List<ConfiguredExpression> mu2Expressions = new ArrayList<ConfiguredExpression>();
		List<ConfiguredExpression> expressions = configurationLoader.getConfigurations().getExpressions();
		for (ConfiguredExpression configuredExpression : expressions) {
			List<ConfiguredValidator> configuredValidators = configuredExpression.getConfiguredValidators();
			for (ConfiguredValidator configuredValidator : configuredValidators) {
				if (configuredValidator.getScope() != null && configuredValidator.getScope().equals("1.1")) {
					mu2Expressions.add(configuredExpression);
					break;
				}
			}
		}
		logger.info("Number of MU2 expressions:" + mu2Expressions.size());
		return mu2Expressions;
	}

	// ------------------------- INTERNAL CODE CHANGE END --------------------------
}
