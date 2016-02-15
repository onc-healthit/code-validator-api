package org.sitenv.vocabularies.configuration;

import org.apache.commons.dbcp.BasicDataSource;
import org.sitenv.vocabularies.loader.VocabularyLoadRunner;
import org.sitenv.vocabularies.loader.VocabularyLoaderFactory;
import org.sitenv.vocabularies.validation.VocabularyValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import java.util.*;

/**
 * Created by Brian on 2/5/2016.
 */
@Configuration
@EnableTransactionManagement
@EnableCaching
@ComponentScan("org.sitenv.vocabularies")
@EnableJpaRepositories("org.sitenv.vocabularies.validation.repositories")
public class CodeValidatorApiConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CodeValidatorApiConfiguration.class);
    private static final String HSQL_JDBC_URL_TEMPLATE = "jdbc:hsqldb:file:E:/Brian/Development/Environment/databases/vocabulary/db;hsqldb.default_table_type=cached;hsqldb.write_delay_millis=10;readonly=false";
    @Value("classpath:schema.sql")
    private Resource HSQL_SCHEMA_SCRIPT;

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
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.show_sql", "false");
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

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(HSQL_JDBC_URL_TEMPLATE);
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setInitialSize(3);
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
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

    @Bean
    public ServiceLocatorFactoryBean vocabularyValidatorFactoryServiceLocatorFactoryBean() {
        ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
        bean.setServiceLocatorInterface(VocabularyValidatorFactory.class);
        return bean;
    }

    @Bean
    public VocabularyValidatorFactory vocabularyValidatorFactory() {
        return (VocabularyValidatorFactory) vocabularyValidatorFactoryServiceLocatorFactoryBean().getObject();
    }

    @Autowired
    @Bean
    VocabularyLoadRunner vocabularyLoadRunner(final Environment environment, final VocabularyLoaderFactory vocabularyLoaderFactory, final  DataSourceInitializer dataSourceInitializer, final DataSource dataSource){
        VocabularyLoadRunner vocabularyLoadRunner = null;
        boolean loadVocabulariesAtStartup = Boolean.valueOf(environment.getProperty("vocabulary.loadVocabulariesAtStartup"));
        String localCodeRepositoryDir = environment.getProperty("vocabulary.localCodeRepositoryDir");
        String localValueSetRepositoryDir = environment.getProperty("vocabulary.localValueSetRepositoryDir");
        if(loadVocabulariesAtStartup){
            vocabularyLoadRunner = new VocabularyLoadRunner();
            System.out.println("LOADING VOCABULARY DATABASES FROM THE FOLLOWING RESOURCES: VALUESETS - " + localValueSetRepositoryDir + " CODES - " + localCodeRepositoryDir);
            vocabularyLoadRunner.setCodeDirectory(localCodeRepositoryDir);
            vocabularyLoadRunner.setValueSetDirectory(localValueSetRepositoryDir);
            vocabularyLoadRunner.setDataSource(dataSource);
            vocabularyLoadRunner.setVocabularyLoaderFactory(vocabularyLoaderFactory);
        }
        return vocabularyLoadRunner;
    }

    @Bean
    public List<ConfiguredExpression> vocabularyValidationConfigurations(ValidationConfigurationLoader configurationLoader){
        return configurationLoader.getConfigurations().getExpressions();
    }

    @Bean
    public DocumentBuilder documentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory domFactory =  DocumentBuilderFactory.newInstance();
        return domFactory.newDocumentBuilder();
    }

    @Bean
    public XPathFactory xPathFactory(){
        return XPathFactory.newInstance();
    }

    @Autowired
    @Bean
    public ValidationConfigurationLoader validationConfigurationLoader(final Environment environment){
        ValidationConfigurationLoader validationConfigurationLoader = new ValidationConfigurationLoader();
        validationConfigurationLoader.setValidationConfigurationFilePath(environment.getProperty("referenceccda.configFile"));
        validationConfigurationLoader.setMarshaller(castorMarshaller());
        validationConfigurationLoader.setUnmarshaller(castorMarshaller());
        return validationConfigurationLoader;
    }

    @Bean
    public Jaxb2Marshaller castorMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan("org.sitenv.vocabularies.configuration");
        Map<String,Object> map = new HashMap<>();
        map.put("jaxb.formatted.output", true);
        jaxb2Marshaller.setMarshallerProperties(map);
        return jaxb2Marshaller;
    }

    @Bean
    CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("loadedValuesets")));
        return cacheManager;
    }
}
