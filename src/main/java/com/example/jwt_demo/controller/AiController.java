package com.example.jwt_demo.controller;


import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.ai.*;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/Ai")
public class AiController {

    private final ObjectMapper mapper;

    public AiController(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @PostMapping("/getAiFillText")
    public <T> T fillDataUsingAi(@RequestBody AiQuestion aiQuestion)  {



        Class<T> referenceClass = (Class<T>) switch (aiQuestion.getReferenceToDataNeeded()) {
            case "Materials" -> MaterialAiDto.class;
            case "Employees" -> EmployeeAiDto.class;
            case "Orders" -> OrderAiDto.class;
            default -> throw new IllegalArgumentException("Unknown type");
        };


        T value = null;


        try {

            HttpClient client = HttpClient.newHttpClient();

            String json = mapper.writeValueAsString(
                    Map.of(
                            "model", "qwen3:4b-instruct",
                            "prompt", aiQuestion.getPrompt(),
                            "stream", false
                    )
            );


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(20))
                    .build();


            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("================================");
            System.out.println(response.body());
            System.out.println("=================================");

            AiResponse aiResponse = mapper.readValue(response.body(), AiResponse.class);

            value = mapper.readValue(aiResponse.getResponse(), referenceClass);


        }catch (Exception e){

            throw  new ValidationException("Something went wrong with your Ai request ", Warnings.ERROR);

        }



        return value;

    }








}
