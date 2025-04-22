package org.example.advertisingagency;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
    @GetMapping
    public String getHome(){
        return "hello world";
    }
}
