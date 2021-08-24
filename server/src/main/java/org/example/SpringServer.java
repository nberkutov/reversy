package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.models.DataBaseDao;
import org.example.services.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example"})
@EntityScan(basePackages = {"org.example"})
@EnableJpaRepositories(basePackages = {"org.example"})
@Slf4j
public class SpringServer implements CommandLineRunner {

    @Autowired
    private DataBaseDao dbo;

    @Autowired
    private Server server;

    public static void main(final String[] args) {
        SpringApplication.run(SpringServer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("The server can now accept clients");
        server.start();
    }
}
