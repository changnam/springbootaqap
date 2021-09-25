package com.honsoft.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.honsoft.repository",entityManagerFactoryRef = "mysqlEntityManagerFactory",transactionManagerRef = "mysqlJpaTransactionManager")
public class MysqlDataSourceConfig {
	
	@Autowired
	private Environment env;
	
	@Bean
	@ConfigurationProperties(prefix = "mysql.datasource")
	public DataSourceProperties mysqlDataSourceProperties() {
		DataSourceProperties properties = new DataSourceProperties();
		return properties;
	}
	
	@Bean
	public DataSource mysqlDataSource() {
		return mysqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Bean
	public DataSourceInitializer mysqlDataSourceInitializer() {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(mysqlDataSource());
		initializer.setEnabled(env.getProperty("mysql.datasource.initialize",Boolean.class,false));
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setSqlScriptEncoding("UTF-8");
		populator.addScript(new ClassPathResource("classpath:/sql/schema-mysql.sql"));
		populator.addScript(new ClassPathResource("classpath:/sql/data-mysql.sql"));
		
		initializer.setDatabasePopulator(populator);
		
		return initializer;
	}
	
	@Bean
	public PlatformTransactionManager mysqlJpaTransactionManager() {
		EntityManagerFactory factory = mysqlEntityManagerFactory().getObject();
		JpaTransactionManager txManager = new JpaTransactionManager(factory);
		return txManager;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(mysqlDataSource());
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.setJpaDialect(new HibernateJpaDialect());
		factory.setPersistenceUnitName("mysqlUnit");
		
		Properties properties = new Properties();
		properties.put("hiberntae.hbm2ddl.auto",env.getProperty("mysql.jpa.hibernate.hbm2ddl.auto") );
		properties.put("hibernate.show_sql",env.getProperty("mysql.jpa.hibernate.show_sql") );
		properties.put("hibernate.format_sql", env.getProperty("mysql.jpa.hibernate.format_sql"));
		properties.put("hibernate.dialect", env.getProperty("mysql.jpa.hibernate.dialect"));
		
		factory.setJpaProperties(properties);
		factory.setPackagesToScan("com.honsoft.entity");
		
		return factory;
	}
}
