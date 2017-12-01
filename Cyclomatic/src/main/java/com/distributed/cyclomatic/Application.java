package com.distributed.cyclomatic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;

import javax.servlet.Filter;
import com.distributed.cyclomatic.security.MyCorsFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	
	/*
	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory() {
	    return new HibernateJpaSessionFactoryBean();
   	}
	*/

   	@Bean
    public FilterRegistrationBean myFilter() {	
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter myFilter = new MyCorsFilter();
        registration.setFilter(myFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(-1000); 
        return registration;
    }
   	
   	
}