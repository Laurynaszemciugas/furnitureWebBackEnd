package com.example.jwt_demo;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class JwtDemoApplication {

	@SneakyThrows
    public static void main(String[] args) {




		SpringApplication.run(JwtDemoApplication.class, args);

		Path folder = Paths.get("uploads/products");

		Files.createDirectories(folder);

		Path file = Paths.get("uploads/products/tests212.png");

		Files.createFile(file);

	}

}
