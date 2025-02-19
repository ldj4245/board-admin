package com.leedae.boardandadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class BoardAndAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardAndAdminApplication.class, args);
    }

}
