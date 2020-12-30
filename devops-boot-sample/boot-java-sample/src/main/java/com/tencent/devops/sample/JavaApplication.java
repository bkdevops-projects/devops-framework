package com.tencent.devops.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JavaApplication {

    @RequestMapping
    public String greeting() {
        return "Hello, Devops!";
    }


    public static void main(String[] args) {
        SpringApplication.run(JavaApplication.class, args);
    }
}
