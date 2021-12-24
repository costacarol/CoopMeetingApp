package com.github.costacarol.coopmeeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CoopMeetingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoopMeetingApplication.class, args);
	}

}
