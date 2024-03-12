package xyz.shanmugavel.poc.springboot.client.restclient;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;
import xyz.shanmugavel.poc.springboot.client.restclient.model.User;
import xyz.shanmugavel.poc.springboot.client.restclient.service.SecuredClientCredService;
import xyz.shanmugavel.poc.springboot.client.restclient.service.UnsecuredUserService;

@SpringBootApplication
@Slf4j
public class RestClientApplication {


	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(RestClientApplication.class, args);
		UnsecuredUserService unsecuredClient = applicationContext.getBean(UnsecuredUserService.class);	
		SecuredClientCredService securedClientCred = applicationContext.getBean(SecuredClientCredService.class);

		String response = securedClientCred.invokeReadAPI();
		log.info("response={}", response);

		List<User> users = unsecuredClient.getAllUsers();
		log.info("# of users={}", users.size());
		log.info("users={}", users);
	}

}
