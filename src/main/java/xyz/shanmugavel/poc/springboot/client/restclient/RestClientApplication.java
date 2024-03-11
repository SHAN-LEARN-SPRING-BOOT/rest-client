package xyz.shanmugavel.poc.springboot.client.restclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;
import xyz.shanmugavel.poc.springboot.client.restclient.service.UnsecuredUserService;

@SpringBootApplication
@Slf4j
public class RestClientApplication {


	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(RestClientApplication.class, args);
		UnsecuredUserService unsecuredClient = applicationContext.getBean(UnsecuredUserService.class);	
		log.info("AllUsers={}", unsecuredClient.getAllUsers());

	}

}
