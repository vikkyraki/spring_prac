package com.example.rakesh.controlllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/name")
    public String secondIndex(@RequestParam String name) {
        return "Greetings from " + name;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from Rakesh";
    }
}
