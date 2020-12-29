package com.tencent.devops.sample;

import com.tencent.devops.demo.GreetingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class JavaApplication {

    private final GreetingService greetingService;

    public JavaApplication(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @RequestMapping
    public String greeting() {
        return greetingService.greeting();
    }


    public static void main(String[] args) {
        SpringApplication.run(JavaApplication.class, args);
    }
}
