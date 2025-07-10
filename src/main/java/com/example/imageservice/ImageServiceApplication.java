package com.example.imageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ImageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageServiceApplication.class, args);
	}

}
