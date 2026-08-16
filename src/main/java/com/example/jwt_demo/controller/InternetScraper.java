package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.InternetScraper.PriceResult;
import com.example.jwt_demo.Common.InternetScraper.Scraper;
import com.example.jwt_demo.DTOS.Order.ComboBoxEmployees;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/internetScraper")
public class InternetScraper {


    @Autowired
    Scraper scraper;

    @GetMapping("/getDataFromInternetScraping/{product}")
    public ResponseEntity<List<PriceResult>> getMiniEmployeeData(@PathVariable String product){
        try {
            return ResponseEntity.ok(scraper.scraper(product));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
