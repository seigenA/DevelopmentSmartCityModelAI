package ai.smartcity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.cache.annotation.EnableCaching
public class SmartCityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartCityApplication.class, args);
    }
}

