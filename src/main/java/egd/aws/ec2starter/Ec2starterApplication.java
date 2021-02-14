package egd.aws.ec2starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Ec2starterApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ec2starterApplication.class, args);
	}

}
