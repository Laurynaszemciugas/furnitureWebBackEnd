package com.example.jwt_demo.Common.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponse {

    private String model;
    private String response;


    public AiResponse(String model, String response) {
        this.model = model;
        this.response = response;
    }


    public AiResponse() {
    }

    public String getModel() {
        return model;
    }

    public String getResponse() {
        return response;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "AiResponse{" +
                "model='" + model + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}


