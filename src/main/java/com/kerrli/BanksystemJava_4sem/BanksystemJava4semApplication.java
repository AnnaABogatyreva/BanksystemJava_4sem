package com.kerrli.BanksystemJava_4sem;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BanksystemJava4semApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(BanksystemJava4semApplication.class, args);
	}

}
