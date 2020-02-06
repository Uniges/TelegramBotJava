package org.eugene.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Application entry point
 */
@SpringBootApplication
@EnableScheduling
@EntityScan("org.eugene.telegram.dao")
@EnableJpaRepositories("org.eugene.telegram.dao")
public class Main {
    private static final String PORT = System.getenv("PORT");
    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();
        SpringApplication.run(Main.class, args);

        try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(PORT))) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
