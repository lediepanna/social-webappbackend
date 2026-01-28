package se.jensen.quyen.socialappbackend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot! Server is working.";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint. No authentication needed.";
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "This is a protected endpoint. You need a JWT token to see this.";
    }
}