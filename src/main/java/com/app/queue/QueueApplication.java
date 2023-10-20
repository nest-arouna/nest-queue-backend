package com.app.queue;

import com.app.queue.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class QueueApplication implements CommandLineRunner {
    @Autowired
	private UserService  userService;
	public static void main(String[] args) {
		SpringApplication.run(QueueApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
      this.userService.initAccount();
	}

	@Bean
	public BCryptPasswordEncoder getBCPE() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public ModelMapper Mapper() {
		return new ModelMapper();
	}


	@Configuration
	@EnableWebMvc
	public class WebConfi implements WebMvcConfigurer {
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**");
		}
	}
}
