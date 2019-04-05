package io.github.gotonode.gem.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@ComponentScan(basePackages = {"io.github.gotonode.gem.controller", "io.github.gotonode.gem.repository"})
@SpringBootApplication
public class Main {

    public static final int MAX_DATABASE_ENTRIES = 10_000; // Limitation imposed by free Heroku dyno.

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
