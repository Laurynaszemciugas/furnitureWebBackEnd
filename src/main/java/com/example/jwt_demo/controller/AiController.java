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
            case "Products" -> ProductAiDto.class;
            default -> throw new IllegalArgumentException("Unknown type");
        };


        T value = null;


        try {

            System.out.println("1");

            HttpClient client = HttpClient.newHttpClient();

            String json = mapper.writeValueAsString(
                    Map.of(
                            "model", "qwen3:4b-instruct",
                            "prompt", aiQuestion.getPrompt(),
                            "stream", false
                    )
            );

            System.out.println("2");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(20))
                    .build();

            System.out.println("3");

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("4");

            System.out.println("================================");
            System.out.println(response.body());
            System.out.println("=================================");

            System.out.println("5");

            AiResponse aiResponse = mapper.readValue(response.body(), AiResponse.class);

            System.out.println("6");

            value = mapper.readValue(aiResponse.getResponse(), referenceClass);

            System.out.println("7");



        }catch (Exception e){

            fillDataUsingAi(aiQuestion);
            System.out.println("AI RESPONSE FAILED RETRYING");
//           throw  new ValidationException("Something went wrong with your Ai request RETRYING ", Warnings.ERROR);
            System.out.println(e);

        }



        return value;

    }








}
