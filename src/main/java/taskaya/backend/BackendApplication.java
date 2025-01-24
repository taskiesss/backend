package taskaya.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import taskaya.backend.entity.User;

import java.util.UUID;

@SpringBootApplication
public class 	BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

//@Component
// class MyCommandLineRunner implements CommandLineRunner {
//
//	@Override
//	public void run(String... args) throws Exception {
//		User user = new User();
//		System.out.println(user.getId());
//	}
//}

