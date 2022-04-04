package com.exist.rbank.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskResource {
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}