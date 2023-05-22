package com.damon.springboot.treestructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SpringbootApplication {

	public static void main(String[] args) {

		TimeZone.setDefault(TimeZone.getTimeZone("GMT+0000"));

		SpringApplication.run(SpringbootApplication.class, args);
	}

}
