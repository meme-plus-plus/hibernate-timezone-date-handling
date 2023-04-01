package com.tutorial.hibernate.datehandling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.orm.jpa.JpaTransactionManager;

@SpringBootApplication(exclude = JpaTransactionManager.class)
public class DateHandlingApplication {
	public static void main(String[] args) {
		SpringApplication.run(DateHandlingApplication.class, args);
	}
}
