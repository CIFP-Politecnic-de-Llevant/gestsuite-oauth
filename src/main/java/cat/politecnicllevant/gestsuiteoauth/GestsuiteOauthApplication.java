package cat.politecnicllevant.gestsuiteoauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GestsuiteOauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestsuiteOauthApplication.class, args);
	}

}
