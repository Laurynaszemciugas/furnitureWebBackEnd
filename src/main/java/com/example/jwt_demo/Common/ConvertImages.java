package com.example.jwt_demo.Common;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ConvertImages {


    public String saveImage(byte[] imageData) throws IOException {




        Path folder = Paths.get("uploads/materials");

        Files.createDirectories(folder);

        String fileName = UUID.randomUUID() + ".png";

        Path file = folder.resolve(fileName);

        Files.write(file, imageData);

        return "http://localhost:8080/uploads/materials/" + fileName;
    }

}
