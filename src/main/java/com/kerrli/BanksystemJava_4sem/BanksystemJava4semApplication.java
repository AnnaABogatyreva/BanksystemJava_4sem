package com.kerrli.BanksystemJava_4sem;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class BanksystemJava4semApplication {

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		SpringApplication.run(BanksystemJava4semApplication.class, args);
	}

}
